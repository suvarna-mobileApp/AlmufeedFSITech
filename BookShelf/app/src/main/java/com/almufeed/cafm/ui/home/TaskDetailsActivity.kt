package com.almufeed.cafm.ui.home

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.room.Room
import com.almufeed.cafm.R
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.dateFormater
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.domain.utils.isOnline
import com.almufeed.cafm.databinding.ActivityTaskDetailsBinding
import com.almufeed.cafm.datasource.cache.database.BookDatabase
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
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

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class TaskDetailsActivity : AppCompatActivity(), BaseInterface {
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityTaskDetailsBinding
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private lateinit var pd : Dialog
    private lateinit var taskId : String
    private val baseViewModel: BaseViewModel by viewModels()
    //private lateinit var snack: Snackbar
    private lateinit var db: BookDatabase
    companion object {
        var clickedButtonCountBefore = 0
        var clickedButtonCountAfter = 0
    }

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
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.linTool.visibility = View.VISIBLE
        baseViewModel.isNetworkConnected.observe(this) { isNetworkAvailable ->
            showNetworkSnackBar(isNetworkAvailable)
        }
        db = Room.databaseBuilder(this@TaskDetailsActivity, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()


        if(isOnline(this@TaskDetailsActivity)){
            pd = Dialog(this, android.R.style.Theme_Black)
            val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
            pd.window!!.setBackgroundDrawableResource(R.color.transparent)
            pd.setContentView(view)
            pd.show()
            homeViewModel.requestForTask()
            subscribeObservers()
        }else{
            Toast.makeText(this@TaskDetailsActivity,"No Internet Connection", Toast.LENGTH_SHORT).show()
        /*    val taskList = db.bookDao().getTaskFromId(taskId)
            val evenList = db.bookDao().getAllEvents()

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

                val event = db.bookDao().getEventsForTask(taskId)
                if(evenList.size > 0){
                    System.out.println("evenlist " + event + " " + evenList + " " + taskId)
                    if(event.equals("Risk Assessment Completed")){
                        txtAction.text = event
                        btnAccept.text = "Start"
                    }else if(event.equals("Accepted")){
                        txtAction.text = event
                        btnAccept.text = "Start Risk Assessment"
                    }else if(event.equals("Instruction set completed")){
                        txtAction.text = event
                        btnAccept.text = "Complete Task"
                    }else if(event.equals("Started")){
                        txtAction.text = event
                        btnAccept.text = "Continue"
                    }else if(event.equals("Before Task")){
                        txtAction.text = "Before Task Pictures Added"
                        if(clickedButtonCountBefore < 11){
                            btnAccept.text = "Add Pictures"
                        }
                    }else if(event.equals("After Task")){
                        if(clickedButtonCountAfter < 11){
                            btnAccept.text = "Add Pictures"
                        }
                        txtAction.text = "After Task Pictures Added"

                    }else{
                        txtAction.text = event
                    }
                }else{
                    txtAction.text = taskList.LOC
                }
            }*/
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
            if(isOnline(this@TaskDetailsActivity)){
                if(binding.btnAccept.text.toString().equals("Accept")){
                    if(isOnline(this@TaskDetailsActivity)){
                        addEventsViewModel.saveForEvent(taskId,"comments","Accepted")
                        val intent = Intent(this@TaskDetailsActivity, RiskAssessment::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }else{
                        val eventRequest = EventsEntity(
                            id = 0,
                            taskId = taskId,
                            resource = "",
                            Comments = "comments",
                            Events = "Accepted",
                        )
                        db.bookDao().insertEvents(eventRequest)
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
                    intent.putExtra("fromTaskBefore", true)
                    startActivity(intent)
                    finish()
                }else if(binding.btnAccept.text.toString().equals("Add Pictures")){
                    val intent = Intent(this@TaskDetailsActivity, AddAttachmentActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()

                }else if(binding.btnAccept.text.toString().equals("Complete Task")){
                    clickedButtonCountBefore = 0
                    clickedButtonCountAfter = 0
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
            }
        })

        binding.btnAddattachment.setOnClickListener {
            val intent = Intent(this@TaskDetailsActivity, AddAttachmentActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
        }
    }

    private fun subscribeObservers() {

        addEventsViewModel.mySetEventDataSTate.observe(this@TaskDetailsActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    dataState.exception.message
                    if(dataState.exception.message.equals("Authentication failed")){
                        Toast.makeText(this@TaskDetailsActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                        baseViewModel.setToken("")
                        baseViewModel.updateUsername("")
                        Intent(this@TaskDetailsActivity, LoginActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    }else{
                        Toast.makeText(this@TaskDetailsActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    if(dataState.data.Success){

                    }else{

                    }
                }
            }.exhaustive
        }

        homeViewModel.myTaskDataSTate.observe(this@TaskDetailsActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    dataState.exception.message
                    if(dataState.exception.message.equals("Authentication failed")){
                        Toast.makeText(this@TaskDetailsActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                        baseViewModel.setToken("")
                        baseViewModel.updateUsername("")
                        Intent(this@TaskDetailsActivity, LoginActivity::class.java).apply {
                            startActivity(this)
                            finish()
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

                                    if(dataState.data.task[i].LOC.equals("Risk Assessment Completed")){
                                        txtAction.text = dataState.data.task[i].LOC
                                        btnAccept.text = "Add Customer Details"
                                    }else if(dataState.data.task[i].LOC.equals("Accepted")){
                                        txtAction.text = dataState.data.task[i].LOC
                                        btnAccept.text = "Start Risk Assessment"
                                    }else if(dataState.data.task[i].LOC.equals("Started")){
                                        txtAction.text = dataState.data.task[i].LOC
                                        btnAccept.text = "Continue"
                                    }else if(dataState.data.task[i].LOC.equals("Instruction set completed")){
                                        txtAction.text = dataState.data.task[i].LOC
                                        btnAccept.text = "Complete Task"
                                    }else if(dataState.data.task[i].LOC.equals("Started")){
                                        txtAction.text = dataState.data.task[i].LOC
                                        btnAccept.text = "Continue"
                                    }else if(dataState.data.task[i].LOC.equals("Before Task") || dataState.data.task[i].LOC.equals("After Task")){

                                        if(taskId.contains("PPM") && clickedButtonCountBefore < 10){
                                            txtAction.text = "Before Task - " + clickedButtonCountBefore + " Picture Added. " +
                                                    "\nAfter Task - " + clickedButtonCountAfter + " Picture Added."
                                            btnAccept.text = "Add Pictures"
                                        }else if(taskId.contains("PPM") && clickedButtonCountAfter < 10){
                                            txtAction.text = "Before Task - " + clickedButtonCountBefore + " Picture Added. " +
                                                    "\nAfter Task - " + clickedButtonCountAfter + " Picture Added."
                                            btnAccept.text = "Add Pictures"
                                        }else if(taskId.contains("RM") && clickedButtonCountBefore < 5){
                                            txtAction.text = "Before Task - " + clickedButtonCountBefore + " Picture Added. " +
                                                    "\nAfter Task - " + clickedButtonCountAfter + " Picture Added."
                                            btnAccept.text = "Add Pictures"
                                        }else if(taskId.contains("RM") && clickedButtonCountAfter < 5){
                                            txtAction.text = "Before Task - " + clickedButtonCountBefore + " Picture Added. " +
                                                    "\nAfter Task - " + clickedButtonCountAfter + " Picture Added."
                                            btnAccept.text = "Add Pictures"
                                        }else if(taskId.contains("SCH") && clickedButtonCountBefore < 5){
                                            txtAction.text = "Before Task - " + clickedButtonCountBefore + " Picture Added. " +
                                                    "\nAfter Task - " + clickedButtonCountAfter + " Picture Added."
                                            btnAccept.text = "Add Pictures"
                                        }else if(taskId.contains("SCH") && clickedButtonCountAfter < 5){
                                            txtAction.text = "Before Task - " + clickedButtonCountBefore + " Picture Added. " +
                                                    "\nAfter Task - " + clickedButtonCountAfter + " Picture Added."
                                            btnAccept.text = "Add Pictures"
                                        }else{
                                            if(clickedButtonCountAfter == 15 && clickedButtonCountBefore == 15){
                                                btnAddattachment.visibility = View.GONE
                                            }else{
                                                btnAddattachment.visibility = View.VISIBLE
                                            }

                                            txtAction.text = "Before Task - " + clickedButtonCountBefore + " Picture Added. " +
                                                    "\nAfter Task - " + clickedButtonCountAfter + " Picture Added."
                                            btnAccept.text = "Add Instruction Set"
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