package com.almufeed.technician.ui.home.customer

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
import com.almufeed.technician.R
import com.almufeed.technician.business.domain.state.DataState
import com.almufeed.technician.business.domain.utils.exhaustive
import com.almufeed.technician.business.domain.utils.isOnline
import com.almufeed.technician.databinding.ActivityProofOfAttendenceBinding
import com.almufeed.technician.datasource.cache.database.BookDatabase
import com.almufeed.technician.datasource.cache.models.offlineDB.CustomerDetailEntity
import com.almufeed.technician.ui.base.BaseViewModel
import com.almufeed.technician.ui.home.TaskDetailsActivity
import com.almufeed.technician.ui.home.events.AddEventsViewModel
import com.almufeed.technician.ui.login.LoginActivity
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
                if(isOnline(this@ProofOfAttendence)){
                    pd.show()
                    proofOfAttendenceViewModel.requestForCustomer(binding.nameInput.text.toString(),binding.emailInput.text.toString(),binding.mobileEditText.text.toString(), taskId)
                }else{
                    val customerEntity = CustomerDetailEntity(0,
                        binding.nameInput.text.toString(),
                        binding.emailInput.text.toString(),
                        binding.mobileEditText.text.toString(),
                        taskId,
                    )
                    db.bookDao().insertCustomerDetail(customerEntity)
                    db.bookDao().update("Customer Details Added",taskId,"Customer Details Added")
                    db.bookDao().updateLOC("Customer Details Added",taskId)
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
                        addEventsViewModel.saveForEvent(taskId,"Customer Details Added","Customer Details Added")
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