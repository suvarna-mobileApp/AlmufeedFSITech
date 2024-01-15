package com.almufeed.cafm.ui.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.almufeed.cafm.R
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.domain.utils.hideKeyboard
import com.almufeed.cafm.business.domain.utils.isOnline
import com.almufeed.cafm.business.domain.utils.toast
import com.almufeed.cafm.databinding.ActivityTaskBinding
import com.almufeed.cafm.datasource.cache.database.BookDatabase
import com.almufeed.cafm.datasource.cache.models.book.BookEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.GetEventEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.TaskEntity
import com.almufeed.cafm.datasource.network.models.bookList.BookListNetworkResponse
import com.almufeed.cafm.datasource.network.models.tasklist.TaskListResponse
import com.almufeed.cafm.datasource.network.models.token.CreateTokenResponse
import com.almufeed.cafm.ui.base.BaseInterface
import com.almufeed.cafm.ui.base.BaseViewModel
import com.almufeed.cafm.ui.home.adapter.TaskRecyclerAdapter
import com.almufeed.cafm.ui.home.adapter.TaskRecyclerAdapterOffline
import com.almufeed.cafm.ui.home.events.AddEventsViewModel
import com.almufeed.cafm.ui.home.instructionSet.CheckListViewModel
import com.almufeed.cafm.ui.launchpad.DashboardActivity
import com.almufeed.cafm.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_task.recyclerTask
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections
import java.util.Random

@AndroidEntryPoint
class TaskActivity : AppCompatActivity(), BaseInterface {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var taskRecyclerAdapter : TaskRecyclerAdapter
    private lateinit var taskRecyclerAdapterOffline : TaskRecyclerAdapterOffline
    private val homeViewModel: HomeViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private val checkListViewModel: CheckListViewModel by viewModels()
    private lateinit var pd : Dialog
    private lateinit var snack: Snackbar
    private lateinit var taskEntity : TaskEntity
    private lateinit var getInstructionSetEntity : GetInstructionSetEntity
    private lateinit var db : BookDatabase
    private lateinit var getEventEntity: GetEventEntity
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.window!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)

        baseViewModel.isNetworkConnected.observe(this) { isNetworkAvailable ->
            showNetworkSnackBar(isNetworkAvailable)
        }

        lifecycleScope.launch {
            userName = baseViewModel.getUsername()
        }

        db = Room.databaseBuilder(this@TaskActivity, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()

        binding.apply {
            snack = Snackbar.make(
                root,
                getString(R.string.text_network_not_available),
                Snackbar.LENGTH_INDEFINITE
            )

            inputSearchQuery.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_search,
                0,
                0,
                0
            )

            inputSearchQuery.addTextChangedListener(watcher)

            inputSearchQuery.setOnTouchListener(object : View.OnTouchListener {
                val DRAWABLE_RIGHT = 2
                override fun onTouch(v: View?, event: MotionEvent): Boolean {
                    if (event.action === MotionEvent.ACTION_UP) {
                        if (inputSearchQuery.compoundDrawables[DRAWABLE_RIGHT] != null &&
                            event.rawX >= inputSearchQuery.right - inputSearchQuery.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                        ) {
                            inputSearchQuery.setText("")
                            hideKeyboard()
                            inputSearchQuery.clearFocus()
                            return true
                        }
                    }
                    return false
                }
            })
        }

        subscribeObservers()
        subscribeObserversForInstructionSet()
        subscribeObserversForEvents()
    }

    private var watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //myListingsViewModel.getMyList(s.toString())
            if (s != null && s.isNotEmpty()) {
                binding.inputSearchQuery.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_close,
                    0
                )
            } else {
                binding.inputSearchQuery.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_search,
                    0,
                    0,
                    0
                )
            }
        }
    }

    private fun subscribeObserversForInstructionSet() {
        checkListViewModel.myTaskDataSTate.observe(this@TaskActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@TaskActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "Instruction Details: ${dataState.data}")
                    pd.dismiss()

                    //System.out.println("instruction set number of times  " + dataState.data.problem.size)
                    if(dataState.data.problem.size > 0){
                        for (i in dataState.data.problem.indices) {
                            getInstructionSetEntity = GetInstructionSetEntity(
                                0,
                                dataState.data.problem[i].LineNumber,
                                dataState.data.problem[i].Steps,
                                dataState.data.problem[i].FeedbackType,
                                dataState.data.problem[i].Refrecid,
                                dataState.data.problem[i].taskId
                            )
                            db.bookDao().insertInstructionSet(getInstructionSetEntity)
                            System.out.println("number of times inside  " + getInstructionSetEntity)
                        }
                    } else {

                    }
                }

                else -> {}
            }.exhaustive
        }
    }

    private fun subscribeObservers() {
        if(isOnline(this@TaskActivity)){
            homeViewModel.myTaskDataSTate.observe(this@TaskActivity) { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        dataState.exception.message
                        if(dataState.exception.message.equals("Authentication failed")){
                            val mediaType = APIServices.mediaType.toMediaType()
                            val body = APIServices.body.toRequestBody(mediaType)
                            val getToken = APIServices.create().getProducts(body)
                            Log.d("products getToken", getToken.request().body.toString())
                            try{
                                getToken.enqueue(object : Callback<CreateTokenResponse> {
                                    override fun onResponse(
                                        call: Call<CreateTokenResponse>,
                                        response: Response<CreateTokenResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            Log.d("products", response.body().toString())
                                            if (response.body() != null) {
                                                var accessToken = "Bearer " + response.body()!!.access_token
                                                baseViewModel.setToken(response.body()!!.access_token)
                                                val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                                                val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                                                myEdit.putString("token", accessToken)
                                                myEdit.commit()
                                                //accessToken = "Bearer " + response.body()!!.access_token
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    homeViewModel.requestForTask()
                                                }, 1000)
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<CreateTokenResponse>, t: Throwable) {
                                        Log.d("failure", t.message.toString())
                                        Toast.makeText(
                                            this@TaskActivity,
                                            "onFailure: " + t.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            }catch(e:Exception){
                                e.printStackTrace()
                                pd.dismiss()
                                Toast.makeText(this@TaskActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                                baseViewModel.setToken("")
                                baseViewModel.updateUsername("")
                                Intent(this@TaskActivity, LoginActivity::class.java).apply {
                                    startActivity(this)
                                }
                            }
                        }else{
                            Toast.makeText(this@TaskActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    is DataState.Loading -> {
                        if(!dataState.loading){
                            pd.dismiss()
                        } else {
                            pd.show()
                        }
                    }
                    is DataState.Success -> {
                        Log.e("AR_MYBUSS::", "Task Details: ${dataState.data}")
                        pd.dismiss()
                        if(dataState.data.task.isEmpty()){
                            binding.recyclerTask.visibility = View.GONE
                            binding.noDataFoundTv.visibility = View.VISIBLE
                        }else{
                            val dataTaskList = dataState.data.task
                            val taskList = db.bookDao().getAllTask(userName)

                            binding.recyclerTask.visibility = View.VISIBLE
                            binding.noDataFoundTv.visibility = View.GONE

                            binding.recyclerTask.apply {
                                taskRecyclerAdapter = TaskRecyclerAdapter(dataState.data,this@TaskActivity)
                                layoutManager = LinearLayoutManager(this@TaskActivity)
                                recyclerTask.adapter = taskRecyclerAdapter
                            }

                            addEventsViewModel.requestForEvent()

                            val commonSize = minOf(dataTaskList.size, taskList.size)
                            System.out.println("common size " + commonSize + " datasize " + dataTaskList.size + " tasksize " + taskList.size)
                            if(taskList.size > 0){
                                for (i in dataState.data.task.indices) {
                                    db.bookDao().updateTask(dataState.data.task[i].LOC,dataState.data.task[i].TaskId)
                                }

                                for (i in commonSize until dataTaskList.size) {
                                    taskEntity = TaskEntity(0,dataState.data.task[i].id,
                                        dataState.data.task[i].hazard,
                                        dataState.data.task[i].scheduledDate,
                                        dataState.data.task[i].attendDate,
                                        dataState.data.task[i].TaskId,
                                        dataState.data.task[i].ServiceType,
                                        dataState.data.task[i].CustAccount,
                                        dataState.data.task[i].Email,
                                        dataState.data.task[i].Building,
                                        dataState.data.task[i].CustId,
                                        dataState.data.task[i].CustName,
                                        dataState.data.task[i].Location,
                                        dataState.data.task[i].Problem,
                                        dataState.data.task[i].Notes,
                                        dataState.data.task[i].LOC,
                                        dataState.data.task[i].Priority,
                                        dataState.data.task[i].Contract,
                                        dataState.data.task[i].Category,
                                        dataState.data.task[i].Phone,
                                        dataState.data.task[i].Discipline,
                                        dataState.data.task[i].CostCenter,
                                        dataState.data.task[i].Source,
                                        dataState.data.task[i].Asset,
                                        dataState.data.task[i].beforeCount,
                                        dataState.data.task[i].afterCount,
                                        userName)

                                    db.bookDao().insertTask(taskEntity)

                                    val eventRequest = EventsEntity(
                                        id = 0,
                                        taskId = dataState.data.task[i].TaskId.toString(),
                                        resource = "",
                                        Comments = dataState.data.task[i].LOC.toString(),
                                        Events = dataState.data.task[i].LOC.toString(),
                                    )
                                    db.bookDao().insertEvents(eventRequest)

                                    checkListViewModel.requestForStep(dataState.data.task[i].TaskId.toString())
                                }
                            //taskListResponse = dataState.data

                           /* val datalist = arrayListOf("item1", "item2", "item3","item4", "item5")
                            val tasklist = arrayListOf<String>()
                            tasklist.addAll(datalist)

                            datalist.removeAt(4)
                            if (tasklist.size > datalist.size) {
                                val iterator = tasklist.listIterator()

                                while (iterator.hasNext()) {
                                    val element = iterator.next()

                                    if (!datalist.contains(element)) {
                                        iterator.remove()
                                    }
                                }

                                System.out.println("Common size inside delete " + tasklist + " " + datalist)
                            }*/
                            /*if (tasklist.size > datalist.size) {
                                try{
                                    for (i in datalist.indices) {
                                        if (tasklist[i] != datalist[i]) {
                                            tasklist.removeAt(i)
                                        }
                                    }
                                }catch (e : IndexOutOfBoundsException){
                                    tasklist.removeAt(tasklist.size - 1)
                                }

                              *//*  for (i in datalist.indices) {
                                    System.out.println("suvarna Common size inside delete1 " + tasklist + " " + datalist)
                                    if (tasklist[i] != datalist[i]) {
                                        tasklist.removeAt(i)
                                    }
                                }*//*
                                System.out.println("suvarna Common size inside delete " + tasklist + " " + datalist)
                            }*/
                                /*if (tasklist.size > datalist.size) {
                                    for (i in datalist.indices) {
                                    if (tasklist[i] != datalist[i]) {
                                        tasklist.removeAt(i)
                                    }
                                }
                                    System.out.println("common size inside delete " + tasklist + " " + datalist)
                                }*/

                            /* for (i in datalist.size until tasklist.size) {

                                 if (tasklist.size > datalist.size) {
                                     tasklist.removeAt(i)
                                 }
                             }
                            */

                            /* if (tasklist.size > datalist.size) {
                                 for(i in tasklist.indices){
                                     tasklist.removeAt(datalist[i])
                                 }

                                 //tasklist.removeAt(2)
                             }*/

                          /*  val indicesInDatalist = datalist.indices.toSet()
                            tasklist.removeIf {
                                System.out.println("common size inside delete " + indicesInDatalist)
                                tasklist.indexOf(it) !in indicesInDatalist
                            }*/

                                /*for (i in commonSize until taskList.size) {

                                    for (i in dataTaskList.indices) {
                                        if (taskList[i].TaskId != dataTaskList[i].TaskId) {
                                            System.out.println("common size inside delete " + i + " datasize " + taskList[i].TaskId)
                                            db.bookDao().deleteTaskByColumnValue(taskList[i].TaskId)
                                            //db.bookDao().deleteInstructionByColumnValue(taskList[i].TaskId)
                                        }
                                    }

                                    db.bookDao().deleteTaskByColumnValue(taskList[i].TaskId)
                                    //db.bookDao().deleteInstructionByColumnValue(taskList[i].TaskId)
                                    System.out.println("common size inside delete1 " + taskList.size + " " + dataTaskList.size)

                                }*/
                            }else{
                                for (i in dataState.data.task.indices) {
                                    taskEntity = TaskEntity(0,dataState.data.task[i].id,
                                        dataState.data.task[i].hazard,
                                        dataState.data.task[i].scheduledDate,
                                        dataState.data.task[i].attendDate,
                                        dataState.data.task[i].TaskId,
                                        dataState.data.task[i].ServiceType,
                                        dataState.data.task[i].CustAccount,
                                        dataState.data.task[i].Email,
                                        dataState.data.task[i].Building,
                                        dataState.data.task[i].CustId,
                                        dataState.data.task[i].CustName,
                                        dataState.data.task[i].Location,
                                        dataState.data.task[i].Problem,
                                        dataState.data.task[i].Notes,
                                        dataState.data.task[i].LOC,
                                        dataState.data.task[i].Priority,
                                        dataState.data.task[i].Contract,
                                        dataState.data.task[i].Category,
                                        dataState.data.task[i].Phone,
                                        dataState.data.task[i].Discipline,
                                        dataState.data.task[i].CostCenter,
                                        dataState.data.task[i].Source,
                                        dataState.data.task[i].Asset,
                                        dataState.data.task[i].beforeCount,
                                        dataState.data.task[i].afterCount,
                                        userName)
                                    db.bookDao().insertTask(taskEntity)

                                    checkListViewModel.requestForStep(dataState.data.task[i].TaskId.toString())

                                    val eventRequest = EventsEntity(
                                        id = 0,
                                        taskId = dataState.data.task[i].TaskId.toString(),
                                        resource = "",
                                        Comments = dataState.data.task[i].LOC.toString(),
                                        Events = dataState.data.task[i].LOC.toString(),
                                    )
                                    db.bookDao().insertEvents(eventRequest)
                                    System.out.println("common size1 " + dataState.data.task[i].TaskId)
                                }
                            }

                           // db.close()

                            /*for (i in 0 until commonSize) {
                                if (dataTaskList[i].TaskId == taskList[i].TaskId) {
                                    // Your comparison logic here
                                    // This block will be executed when the taskId of the ith element in both lists is equal
                                    System.out.println("printtasklist data " + dataState.data.task[i].TaskId)
                                    System.out.println("printtasklist data " + taskList[i].TaskId)
                                } else {
                                    // Your logic for when taskId is not equal
                                    System.out.println("printtasklist else " + dataState.data.task[i].TaskId)
                                    //System.out.println("printtasklist task " + taskList[i].TaskId)
                                }
                            }*/

// If needed, you can handle the remaining elements in the larger list separately
                           /* for (i in commonSize until dataTaskList.size) {
                                // Handle remaining elements in dataTaskList
                            }*/
                        }
                    }
                }.exhaustive
            }
            db.close()
        }
    }
    private fun subscribeObserversForEvents() {
        addEventsViewModel.myEventDataSTate.observe(this@TaskActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    Toast.makeText(this@TaskActivity,"Some error, Please try later", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    db.bookDao().deleteTable()
                        for (i in dataState.data.EventList.indices) {
                            getEventEntity = GetEventEntity(
                                0,
                                dataState.data.EventList[i].TaskEvent
                            )
                            db.bookDao().insertGetEvents(getEventEntity)
                            System.out.println("number of times inside  " + getEventEntity)
                        }
                    }
                }
            }.exhaustive
        }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            if (snack.isShown) {
                this.toast(getString(com.almufeed.cafm.R.string.text_network_connected))
            }
        } else {
            snack.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if(isOnline(this@TaskActivity)){
            pd.show()
            homeViewModel.requestForTask()
        }else{
            val taskList = db.bookDao().getAllTask(userName)
            if(taskList.size > 0){
                binding.recyclerTask.visibility = View.VISIBLE
                binding.noDataFoundTv.visibility = View.GONE
                binding.recyclerTask.apply {
                    taskRecyclerAdapterOffline = TaskRecyclerAdapterOffline(taskList,this@TaskActivity)
                    layoutManager = LinearLayoutManager(this@TaskActivity)
                    recyclerTask.adapter = taskRecyclerAdapterOffline
                }
                db.close()
            }else{
                binding.recyclerTask.visibility = View.GONE
                binding.noDataFoundTv.visibility = View.VISIBLE
                binding.noDataFoundTv.text = "No Internet Connection"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            Intent(this@TaskActivity, DashboardActivity::class.java).apply {
                startActivity(this)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}