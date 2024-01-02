package com.android.almufeed.ui.home

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
import com.android.almufeed.R
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.business.domain.utils.isOnline
import com.android.almufeed.databinding.ActivityRiskAssessmentBinding
import com.android.almufeed.datasource.cache.database.BookDatabase
import com.android.almufeed.ui.home.events.AddEventsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RiskAssessment : AppCompatActivity() {
    private lateinit var binding: ActivityRiskAssessmentBinding
    private lateinit var taskId: String
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

        subscribeObservers()
        binding.btnSafe.setOnClickListener (View.OnClickListener { view ->

            if((binding.checkBoxCheck1.isChecked || binding.checkBoxCheck2.isChecked) &&
                (binding.checkBoxTool1.isChecked || binding.checkBoxTool2.isChecked)){
                if(binding.checkBoxCheck1.isChecked && binding.checkBoxTool1.isChecked &&
                    !binding.checkBoxCheck2.isChecked && !binding.checkBoxTool2.isChecked){
                    if(binding.message.text.toString().isEmpty()){
                        Toast.makeText(this@RiskAssessment,"Add Comments", Toast.LENGTH_SHORT).show()
                    }else{
                        pd = Dialog(this, android.R.style.Theme_Black)
                        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
                        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
                        pd.setContentView(view)
                        pd.show()
                        if(isOnline(this@RiskAssessment)){
                            addEventsViewModel.saveForEvent(taskId,binding.message.text.toString(),"Risk Assessment Completed")
                        }else{
                            pd.dismiss()
                            db.bookDao().update("Risk Assessment Completed",taskId,binding.message.text.toString())
                            val intent = Intent(this@RiskAssessment, TaskDetailsActivity::class.java)
                            intent.putExtra("status", "Risk Assessment Completed")
                            intent.putExtra("taskid", taskId)
                            startActivity(intent)
                            finish()
                        }
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
    }

    private fun subscribeObservers() {

        addEventsViewModel.mySetEventDataSTate.observe(this@RiskAssessment) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    dataState.exception.message
                        Toast.makeText(this@RiskAssessment,dataState.exception.message, Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    if(dataState.data.Success){
                        val intent = Intent(this@RiskAssessment, TaskDetailsActivity::class.java)
                        intent.putExtra("status", "Risk Assessment Completed")
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@RiskAssessment,"please try later", Toast.LENGTH_SHORT).show()
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