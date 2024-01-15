package com.almufeed.cafm.ui.home

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.almufeed.cafm.R
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.clickedButtonCountAfter
import com.almufeed.cafm.business.domain.utils.clickedButtonCountBefore
import com.almufeed.cafm.business.domain.utils.clickedButtonCountInspection
import com.almufeed.cafm.business.domain.utils.clickedButtonCountMaterialPicture
import com.almufeed.cafm.business.domain.utils.dateFormater
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.domain.utils.isOnline
import com.almufeed.cafm.business.domain.utils.maxImage
import com.almufeed.cafm.business.domain.utils.minImagePPM
import com.almufeed.cafm.business.domain.utils.minImageRM
import com.almufeed.cafm.databinding.ActivityTaskDetailsBinding
import com.almufeed.cafm.datasource.cache.database.BookDatabase
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.cafm.datasource.network.models.token.CreateTokenResponse
import com.almufeed.cafm.ui.base.BaseInterface
import com.almufeed.cafm.ui.base.BaseViewModel
import com.almufeed.cafm.ui.home.attachment.AddAttachmentActivity
import com.almufeed.cafm.ui.home.attachment.AttachmentList
import com.almufeed.cafm.ui.home.customer.ProofOfAttendence
import com.almufeed.cafm.ui.home.events.AddEventsActivity
import com.almufeed.cafm.ui.home.events.AddEventsViewModel
import com.almufeed.cafm.ui.home.instructionSet.CheckListActivity
import com.almufeed.cafm.ui.home.rateus.RatingActivity
import com.almufeed.cafm.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class TaskDetailsActivity : AppCompatActivity(), BaseInterface {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityTaskDetailsBinding
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private lateinit var pd : Dialog
    private lateinit var taskId : String
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var db: BookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()
        taskId = intent.getStringExtra("taskid").toString()

        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Task : $taskId"
            binding.toolbar.aboutus.text = "Task :  $taskId"
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.linTool.visibility = View.VISIBLE
        baseViewModel.isNetworkConnected.observe(this) { isNetworkAvailable ->
            showNetworkSnackBar(isNetworkAvailable)
        }

        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.window!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)

        db = Room.databaseBuilder(this@TaskDetailsActivity, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()

        if(isOnline(this@TaskDetailsActivity)){
            pd.show()
            homeViewModel.requestForTask()

        }else{
            //Toast.makeText(this@TaskDetailsActivity,"No Internet Connection", Toast.LENGTH_SHORT).show()
            val taskList = db.bookDao().getTaskFromId(taskId)

            binding.apply {
                toolbar.aboutus.text = "Task :  $taskId"
                txtTaskid.text = "Task Id : $taskId"
                txtShortNote.text = "Task Description : ${taskList.Notes}"
                txtpro.text = "Priority : ${taskList.Priority}"
                txtsceduleDate.text = "Scheduled Date : " + dateFormater(taskList.scheduledDate)
                txtContactname.text = "Name : ${taskList.CustName}"
                txtNumber.text = "Mobile Number : ${taskList.Phone}"
                txtBuilding.text = "Building : ${taskList.Building}"
                txtLocation.text = "Location : ${taskList.Location}"
                txtDate.text = "Due Date :  " + dateFormater(taskList.attendDate)
                txtAction.text = taskList.LOC

                if(taskList.LOC.equals("Risk Assessment Completed")){
                    btnAccept.text = "Add Customer Details"
                }else if(taskList.LOC.equals("Accepted")){
                    btnAccept.text = "Start Risk Assessment"
                }else if(taskList.LOC.equals("Instruction set completed")){
                    btnAccept.text = "Complete Task"
                }else if(taskList.LOC.equals("Customer Details Added")){
                    btnAccept.text = "Continue"
                }else if(taskList.LOC.equals("Before Task") || taskList.LOC.equals("After Task")){

                    lifecycleScope.launch {
                        if(taskList.BeforeCount > 0 || taskList.AfterCount > 0){
                            txtAction.text = "Before Task - " + taskList.BeforeCount + " Picture Added. " +
                                    "\nAfter Task - " + taskList.AfterCount + " Picture Added."

                            if(taskId.contains("RM") && taskList.BeforeCount < minImageRM){
                                btnAccept.text = "Add Pictures"
                            }else if(taskId.contains("RM") && taskList.AfterCount < minImageRM){
                                btnAccept.text = "Add Pictures"
                            }else if(taskList.BeforeCount < minImagePPM){
                                btnAccept.text = "Add Pictures"
                            }else if(taskList.AfterCount < minImagePPM){
                                btnAccept.text = "Add Pictures"
                            }else{
                                if(taskList.AfterCount == maxImage && taskList.BeforeCount == maxImage){
                                    btnAddattachment.visibility = View.GONE
                                }else{
                                    btnAddattachment.visibility = View.VISIBLE
                                }
                                btnAccept.text = "Add Instruction Set"
                            }
                        }else{
                            txtAction.text = "Before Task - " + baseViewModel.getBeforeCount() + " Picture Added. " +
                                    "\nAfter Task - " + baseViewModel.getAfterCount() + " Picture Added."

                            if(taskId.contains("RM") && baseViewModel.getBeforeCount() < minImageRM){
                                btnAccept.text = "Add Pictures"
                            }else if(taskId.contains("RM") && baseViewModel.getAfterCount() < minImageRM){
                                btnAccept.text = "Add Pictures"
                            }else if(baseViewModel.getBeforeCount() < minImagePPM){
                                btnAccept.text = "Add Pictures"
                            }else if(baseViewModel.getAfterCount() < minImagePPM){
                                btnAccept.text = "Add Pictures"
                            }else{
                                if(baseViewModel.getAfterCount() == maxImage && baseViewModel.getBeforeCount() == maxImage){
                                    btnAddattachment.visibility = View.GONE
                                }else{
                                    btnAddattachment.visibility = View.VISIBLE
                                }
                                btnAccept.text = "Add Instruction Set"
                            }
                        }
                    }
                }else if(taskList.LOC.equals("Material Picture") || taskList.LOC.equals("Inspection")){
                    if(clickedButtonCountMaterialPicture == minImageRM){
                        btnAddattachment.visibility = View.GONE
                        btnAccept.text = "Add Instruction Set"
                    }else if(clickedButtonCountInspection == minImageRM){
                        btnAddattachment.visibility = View.GONE
                        btnAccept.text = "Add Instruction Set"
                    }else{
                        btnAddattachment.visibility = View.VISIBLE
                        btnAccept.text = "Add Pictures"
                    }
                }
            }
        }

        binding.toolbar.incToolbarEvent.setOnClickListener (View.OnClickListener {
            val intent = Intent(this@TaskDetailsActivity, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        binding.toolbar.incToolbarAttachment.setOnClickListener (View.OnClickListener {
            val intent = Intent(this@TaskDetailsActivity, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        binding.btnAccept.setOnClickListener (View.OnClickListener { view ->
           // if(isOnline(this@TaskDetailsActivity)){
                if(binding.btnAccept.text.toString().equals("Accept")){
                    if(isOnline(this@TaskDetailsActivity)){
                        addEventsViewModel.saveForEvent(taskId,"comments","Accepted")
                        val intent = Intent(this@TaskDetailsActivity, RiskAssessment::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }else{
                        db.bookDao().update("Accepted",taskId,"comments")
                        db.bookDao().updateLOC("Accepted",taskId)
                        val intent = Intent(this@TaskDetailsActivity, RiskAssessment::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }

                }else if(binding.btnAccept.text.toString().equals("Add Customer Details")){
                    val intent = Intent(this@TaskDetailsActivity, ProofOfAttendence::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()

                }else if(binding.btnAccept.text.toString().equals("Start")){
                    if(isOnline(this@TaskDetailsActivity)){
                        addEventsViewModel.saveForEvent(taskId,"comments","Started")
                        if(binding.txtAction.text == "Start"){
                            val intent = Intent(this@TaskDetailsActivity, AddAttachmentActivity::class.java)
                            intent.putExtra("taskid", taskId)
                            intent.putExtra("fromTaskBefore", true)
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        db.bookDao().update("Started",taskId,"comments")
                        db.bookDao().updateLOC("Started",taskId)
                        if(binding.txtAction.text == "Start"){
                            val intent = Intent(this@TaskDetailsActivity, AddAttachmentActivity::class.java)
                            intent.putExtra("taskid", taskId)
                            intent.putExtra("fromTaskBefore", true)
                            startActivity(intent)
                            finish()
                        }
                    }

                }else if(binding.btnAccept.text.toString().equals("Continue")){
                    val intent = Intent(this@TaskDetailsActivity, AddAttachmentActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }else if(binding.btnAccept.text.toString().equals("Add Pictures")){
                    val intent = Intent(this@TaskDetailsActivity, AddAttachmentActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()

                }else if(binding.btnAccept.text.toString().equals("Complete Task")){
                    val intent = Intent(this@TaskDetailsActivity, RatingActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }else if(binding.btnAccept.text.toString().equals("Add Instruction Set")){
                    val intent = Intent(this@TaskDetailsActivity, CheckListActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }else if(binding.btnAccept.text.toString().equals("Start Risk Assessment")){
                    val intent = Intent(this@TaskDetailsActivity, RiskAssessment::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }
            //}
        })

        binding.btnAddattachment.setOnClickListener {
            val intent = Intent(this@TaskDetailsActivity, AddAttachmentActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {

        addEventsViewModel.mySetEventDataSTate.observe(this@TaskDetailsActivity) { dataState ->
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
                                    Toast.makeText(this@TaskDetailsActivity, "onFailure: " + t.message, Toast.LENGTH_SHORT).show()
                                }
                            })
                        }catch(e:Exception){
                            e.printStackTrace()
                            pd.dismiss()
                            Toast.makeText(this@TaskDetailsActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                            baseViewModel.setToken("")
                            baseViewModel.updateUsername("")
                            Intent(this@TaskDetailsActivity, LoginActivity::class.java).apply {
                                startActivity(this)
                            }
                        }
                    }else{
                        Toast.makeText(this@TaskDetailsActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                }
            }.exhaustive
        }

        homeViewModel.myTaskDataSTate.observe(this@TaskDetailsActivity) { dataState ->
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
                                    Toast.makeText(this@TaskDetailsActivity, "onFailure: " + t.message, Toast.LENGTH_SHORT).show()
                                }
                            })
                        }catch(e:Exception){
                            e.printStackTrace()
                            pd.dismiss()
                            Toast.makeText(this@TaskDetailsActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                            baseViewModel.setToken("")
                            baseViewModel.updateUsername("")
                            Intent(this@TaskDetailsActivity, LoginActivity::class.java).apply {
                                startActivity(this)
                            }
                        }
                    }else{
                        Toast.makeText(this@TaskDetailsActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
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
                        val intent = Intent(this@TaskDetailsActivity, TaskActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        for (i in dataState.data.task.indices) {
                            if(taskId == dataState.data.task[i].TaskId.toString()){
                                binding.apply {
                                    toolbar.aboutus.text = "Task :  $taskId"
                                    txtTaskid.text = "Task Id : $taskId"
                                    txtShortNote.text = "Task Description : ${dataState.data.task[i].Notes}"
                                    txtpro.text = "Priority : ${dataState.data.task[i].Priority}"
                                    txtsceduleDate.text = "Reported Date : " + dateFormater(dataState.data.task[i].scheduledDate)
                                    txtContactname.text = "Name : ${dataState.data.task[i].CustName}"
                                    txtNumber.text = "Mobile Number : ${dataState.data.task[i].Phone}"
                                    txtBuilding.text = "Building : ${dataState.data.task[i].Building}"
                                    txtLocation.text = "Location : ${dataState.data.task[i].Location}"
                                    txtDate.text = "Due Date :  " + dateFormater(dataState.data.task[i].attendDate)
                                    txtAction.text = dataState.data.task[i].LOC
                                    txtAction.text = "Before Task - " + dataState.data.task[i].beforeCount + " Picture Added. " +
                                            "\nAfter Task - " + dataState.data.task[i].afterCount + " Picture Added."

                                    if(dataState.data.task[i].LOC.equals("Risk Assessment Completed")){
                                        btnAccept.text = "Add Customer Details"
                                    }else if(dataState.data.task[i].LOC.equals("Accepted")){
                                        btnAccept.text = "Start Risk Assessment"
                                    }else if(dataState.data.task[i].LOC.equals("Started")){
                                        btnAccept.text = "Continue"
                                    }else if(dataState.data.task[i].LOC.equals("Instruction set completed")){
                                        btnAccept.text = "Complete Task"
                                    }else if(dataState.data.task[i].LOC.equals("Started")){
                                        btnAccept.text = "Continue"
                                    }else if(dataState.data.task[i].LOC.equals("Before Task") || dataState.data.task[i].LOC.equals("After Task")){
                                        if(taskId.contains("RM") && dataState.data.task[i].beforeCount < minImageRM){
                                            btnAccept.text = "Add Pictures"
                                        }else if(taskId.contains("RM") && dataState.data.task[i].afterCount < minImageRM){
                                            btnAccept.text = "Add Pictures"
                                        }else if(dataState.data.task[i].beforeCount < minImagePPM){
                                            btnAccept.text = "Add Pictures"
                                        }else if(dataState.data.task[i].afterCount < minImagePPM){
                                            btnAccept.text = "Add Pictures"
                                        }else{
                                            if(dataState.data.task[i].afterCount == maxImage && dataState.data.task[i].beforeCount == maxImage){
                                                btnAddattachment.visibility = View.GONE
                                            }else{
                                                btnAddattachment.visibility = View.VISIBLE
                                            }
                                            btnAccept.text = "Add Instruction Set"
                                        }
                                        /*if(taskId.contains("RM") && dataState.data.task[i].beforeCount < minImageRM){
                                            btnAccept.text = "Add Pictures"
                                        }else if(taskId.contains("RM") && dataState.data.task[i].afterCount < minImageRM){
                                            btnAccept.text = "Add Pictures"
                                        }else if(dataState.data.task[i].beforeCount < minImagePPM){
                                            btnAccept.text = "Add Pictures"
                                        }else if(dataState.data.task[i].afterCount < minImagePPM){
                                            btnAccept.text = "Add Pictures"
                                        }else{
                                            if(dataState.data.task[i].afterCount == maxImage && dataState.data.task[i].beforeCount == maxImage){
                                                btnAddattachment.visibility = View.GONE
                                            }else{
                                                btnAddattachment.visibility = View.VISIBLE
                                            }
                                            btnAccept.text = "Add Instruction Set"
                                        } */
                                    }else if(dataState.data.task[i].LOC.equals("Material Picture") || dataState.data.task[i].LOC.equals("Inspection")){
                                        if(clickedButtonCountMaterialPicture == minImageRM){
                                            btnAddattachment.visibility = View.GONE
                                            btnAccept.text = "Add Instruction Set"
                                        }else if(clickedButtonCountInspection == minImageRM){
                                            btnAddattachment.visibility = View.GONE
                                            btnAccept.text = "Add Instruction Set"
                                        }else{
                                            btnAddattachment.visibility = View.VISIBLE
                                            btnAccept.text = "Add Pictures"
                                        }
                                    }else{
                                        txtAction.text = dataState.data.task[i].LOC
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }.exhaustive
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this@TaskDetailsActivity, TaskActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
       /* if (isNetworkAvailable) {
            if (snack.isShown) {
                this.toast(getString(com.android.almufeed.R.string.text_network_connected))
            }
        } else {
            snack.dismiss()
        }*/
    }

    override fun onResume() {
        super.onResume()
    }
}