package com.almufeed.cafm.ui.home.customer

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.almufeed.cafm.databinding.ActivityProofOfAttendenceBinding
import com.almufeed.cafm.datasource.cache.database.BookDatabase
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.cafm.ui.base.BaseViewModel
import com.almufeed.cafm.ui.home.RiskAssessment
import com.almufeed.cafm.ui.home.TaskActivity
import com.almufeed.cafm.ui.home.TaskDetailsActivity
import com.almufeed.cafm.ui.home.events.AddEventsViewModel
import com.almufeed.cafm.ui.home.rateus.RatingActivity
import com.almufeed.cafm.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProofOfAttendence : AppCompatActivity() {
    private lateinit var binding: ActivityProofOfAttendenceBinding
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val proofOfAttendenceViewModel: ProofOfAttendenceViewModel by viewModels()
    private lateinit var taskId : String
    private lateinit var pd : Dialog
    private lateinit var db :BookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProofOfAttendenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()
        taskId = intent.getStringExtra("taskid").toString()
        db = Room.databaseBuilder(this, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()

        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.aboutus.text = "Task : $taskId"
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.incToolbarEvent.visibility = View.GONE
        binding.toolbar.incToolbarAttachment.visibility = View.GONE

        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)

        binding.btnSubmit.setOnClickListener(View.OnClickListener { view ->
            if(binding.nameInput.text.toString().isNotEmpty() && binding.mobileEditText.text.toString().isNotEmpty() && binding.emailInput.text.toString().isNotEmpty()){
                pd.show()
                if(isOnline(this@ProofOfAttendence)){
                    proofOfAttendenceViewModel.requestForCustomer(binding.nameInput.text.toString(),binding.mobileEditText.text.toString(),binding.emailInput.text.toString(), taskId)
                }else{
                    pd.dismiss()
                    val eventRequest = EventsEntity(
                        id = 0,
                        taskId = taskId,
                        resource = "",
                        Comments = "comments",
                        Events = "Customer Details Added",
                    )
                    db.bookDao().insertEvents(eventRequest)
                    val intent = Intent(this@ProofOfAttendence, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }
            }else{
                Toast.makeText(this@ProofOfAttendence,"All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
           /* val intent = Intent(this@ProofOfAttendence, TaskDetailsActivity::class.java)
            intent.putExtra("taskid", taskId)
            intent.putExtra("status", "Add Instruction Steps")
            startActivity(intent)*/
        })

        subscribeObservers()
    }

    private fun subscribeObservers() {

        proofOfAttendenceViewModel.myRateDataSTate.observe(this@ProofOfAttendence) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@ProofOfAttendence,"Something went wrong", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    if(dataState.data.Success){
                        addEventsViewModel.saveForEvent(taskId,"comments","Customer Details Added")
                        val intent = Intent(this@ProofOfAttendence, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    } else {

                    }
                }
            }.exhaustive
        }

        addEventsViewModel.mySetEventDataSTate.observe(this@ProofOfAttendence) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    dataState.exception.message
                    if(dataState.exception.message.equals("Authentication failed")){
                        Toast.makeText(this@ProofOfAttendence,dataState.exception.message, Toast.LENGTH_SHORT).show()
                        baseViewModel.setToken("")
                        baseViewModel.updateUsername("")
                        Intent(this@ProofOfAttendence, LoginActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    }else{
                        Toast.makeText(this@ProofOfAttendence,dataState.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    if(dataState.data.Success){

                    }else{
                        Toast.makeText(this@ProofOfAttendence,"Some error, please try later", Toast.LENGTH_SHORT).show()
                    }
                }
            }.exhaustive
        }
    }
}