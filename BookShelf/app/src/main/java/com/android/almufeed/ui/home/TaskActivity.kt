package com.android.almufeed.ui.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.android.almufeed.R
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.business.domain.utils.isOnline
import com.android.almufeed.business.domain.utils.toast
import com.android.almufeed.databinding.ActivityTaskBinding
import com.android.almufeed.datasource.cache.database.BookDatabase
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity
import com.android.almufeed.datasource.network.models.bookList.BookListNetworkResponse
import com.android.almufeed.datasource.network.models.tasklist.TaskListResponse
import com.android.almufeed.ui.base.BaseInterface
import com.android.almufeed.ui.base.BaseViewModel
import com.android.almufeed.ui.home.adapter.TaskRecyclerAdapter
import com.android.almufeed.ui.home.instructionSet.CheckListViewModel
import com.android.almufeed.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_task.recyclerTask
import java.util.Collections
import java.util.Random

@AndroidEntryPoint
class TaskActivity : AppCompatActivity(), BaseInterface {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var taskRecyclerAdapter : TaskRecyclerAdapter
    private val homeViewModel: HomeViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val checkListViewModel: CheckListViewModel by viewModels()
    private lateinit var pd : Dialog
    private lateinit var snack: Snackbar
    private lateinit var taskListResponse: TaskListResponse
    private lateinit var bookEntity : BookEntity
    private lateinit var taskEntity : TaskEntity
    private lateinit var getInstructionSetEntity : GetInstructionSetEntity

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

        baseViewModel.isNetworkConnected.observe(this) { isNetworkAvailable ->
            showNetworkSnackBar(isNetworkAvailable)
        }

        binding.apply {
            snack = Snackbar.make(
                root,
                getString(R.string.text_network_not_available),
                Snackbar.LENGTH_INDEFINITE
            )
        }

        subscribeObservers()

        binding.container.setOnRefreshListener {
            binding.container.isRefreshing = false
            Collections.shuffle(taskListResponse.task, Random(System.currentTimeMillis()))
            taskRecyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun subscribeObservers() {
        homeViewModel.myTaskDataSTate.observe(this@TaskActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    dataState.exception.message
                    if(dataState.exception.message.equals("Authentication failed")){
                        Toast.makeText(this@TaskActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                        baseViewModel.setToken("")
                        baseViewModel.updateUsername("")
                        Intent(this@TaskActivity, LoginActivity::class.java).apply {
                            startActivity(this)
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
                        binding.recyclerTask.visibility = View.VISIBLE
                        binding.noDataFoundTv.visibility = View.GONE
                        taskListResponse = dataState.data
                        val db = Room.databaseBuilder(this@TaskActivity, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()
                        //db.bookDao().deleteBook(bookEntity)

                        val taskId = db.bookDao().getAllBooks()
                        val taskList = db.bookDao().getAllTask()
                        for (i in dataState.data.task.indices) {
                            if(taskList.size > 0){
                                if(taskList[i].TaskId != dataState.data.task[i].TaskId) {
                                    taskEntity = TaskEntity(
                                        0,
                                        dataState.data.task[i].id,
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
                                        dataState.data.task[i].Asset
                                    )
                                    db.bookDao().insertTask(taskEntity)
                                }
                            }else{
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
                                    dataState.data.task[i].Asset)
                                db.bookDao().insertTask(taskEntity)
                            }

                            if(taskId.isEmpty()){
                                bookEntity = BookEntity(0,dataState.data.task[i].TaskId.toString())
                                db.bookDao().insertBook(bookEntity)
                            }
                        }
                        db.close()

                        binding.recyclerTask.apply {
                            val taskList = db.bookDao().getAllTask()
                            taskRecyclerAdapter = TaskRecyclerAdapter(taskList,this@TaskActivity)
                            layoutManager = LinearLayoutManager(this@TaskActivity)
                            recyclerTask.adapter = taskRecyclerAdapter
                        }
                    }
                }
            }.exhaustive
        }
    }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            if (snack.isShown) {
                this.toast(getString(com.android.almufeed.R.string.text_network_connected))
            }
        } else {
            snack.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if(isOnline(this@TaskActivity)){
            pd = Dialog(this, android.R.style.Theme_Black)
            val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
            pd.window!!.setBackgroundDrawableResource(R.color.transparent)
            pd.setContentView(view)
            pd.show()
            homeViewModel.requestForTask()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun getTaskList(context: Context) : BookListNetworkResponse{
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("tasklist.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: Exception) {
            ioException.stackTrace
        }
        val listTaskType = object : TypeToken<BookListNetworkResponse>() {}.type
        return Gson().fromJson(jsonString, listTaskType)
    }
}