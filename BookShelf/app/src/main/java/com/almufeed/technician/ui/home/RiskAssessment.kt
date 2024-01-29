package com.almufeed.technician.ui.home

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
import com.almufeed.technician.R
import com.almufeed.technician.business.domain.state.DataState
import com.almufeed.technician.business.domain.utils.exhaustive
import com.almufeed.technician.business.domain.utils.isOnline
import com.almufeed.technician.databinding.ActivityRiskAssessmentBinding
import com.almufeed.technician.datasource.cache.database.BookDatabase
import com.almufeed.technician.ui.home.events.AddEventsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RiskAssessment : AppCompatActivity() {
    private lateinit var binding: ActivityRiskAssessmentBinding
    private lateinit var taskId: String
    private var comment: String = "NA"
    private lateinit var pd : Dialog
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private lateinit var db : BookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiskAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Room.databaseBuilder(this, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()

        taskId = intent.getStringExtra("taskid").toString()
        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            binding.toolbar.aboutus.text = "Task : $taskId"
        }

        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)

        binding.btnSafe.setOnClickListener (View.OnClickListener { view ->

            if((binding.checkBoxCheck1.isChecked || binding.checkBoxCheck2.isChecked) &&
                (binding.checkBoxTool1.isChecked || binding.checkBoxTool2.isChecked)){
                if(binding.checkBoxCheck1.isChecked && binding.checkBoxTool1.isChecked &&
                    !binding.checkBoxCheck2.isChecked && !binding.checkBoxTool2.isChecked){
                    pd.show()
                    comment = binding.message.text.toString()
                    if(isOnline(this@RiskAssessment)){
                        addEventsViewModel.saveForEvent(taskId,comment,"Risk Assessment Completed")
                    }else{
                        pd.dismiss()
                        db.bookDao().update("Risk Assessment Completed",taskId,comment)
                        db.bookDao().updateLOC("Risk Assessment Completed",taskId)
                        val intent = Intent(this@RiskAssessment, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }
                }else{
                    Toast.makeText(this@RiskAssessment,"Not safe to continue", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@RiskAssessment,"Check the necessary details", Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnUnsafe.setOnClickListener (View.OnClickListener { view ->

            if((binding.checkBoxCheck1.isChecked || binding.checkBoxCheck2.isChecked) &&
                (binding.checkBoxTool1.isChecked || binding.checkBoxTool2.isChecked)){
                if(!binding.checkBoxCheck1.isChecked && !binding.checkBoxTool1.isChecked &&
                    binding.checkBoxCheck2.isChecked && binding.checkBoxTool2.isChecked){
                    val intent = Intent(this@RiskAssessment, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this@RiskAssessment,"Not safe to continue", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@RiskAssessment,"Check the necessary details", Toast.LENGTH_SHORT).show()
            }
        })

        subscribeObservers()
    }

    private fun subscribeObservers() {

        addEventsViewModel.mySetEventDataSTate.observe(this@RiskAssessment) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@RiskAssessment,dataState.exception.message, Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    if(dataState.data.Success){
                        val intent = Intent(this@RiskAssessment, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@RiskAssessment,"Some error please try later", Toast.LENGTH_SHORT).show()
                    }
                }
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
}