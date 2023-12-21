package com.android.almufeed.ui.home.events

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.android.almufeed.R
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.databinding.ActivityAddEventsBinding
import com.android.almufeed.datasource.network.models.events.GetEventData
import com.android.almufeed.ui.home.TaskActivity
import com.android.almufeed.ui.home.TaskDetailsActivity
import com.android.almufeed.ui.home.attachment.AttachmentList
import com.android.almufeed.ui.home.rateus.RatingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEventsBinding
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private lateinit var pd : Dialog
    var imageType = arrayOf("Select an Event", "Under DLP", "Material Collected and On site"," Customer request to reschedule","Inspection Completed",
    "In progress","Out of scope","Call back requested","Quotation pending","No access")
    private var selectedImageType : Int = -1
    private var selectedImage : Int = -1
    private lateinit var taskId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        taskId = intent.getStringExtra("taskid").toString()
        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.aboutus.text = "Task : $taskId"
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.linTool.visibility = View.VISIBLE
       /* binding.toolbar.incToolbarEvent.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@AddEventsActivity, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })*/

        binding.toolbar.incToolbarAttachment.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@AddEventsActivity, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })
        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.show()
        subscribeObservers()
        binding.spinnerType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedImageType = position - 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.btnSave.setOnClickListener {
            pd.show()
            if(selectedImageType < 0){
                Toast.makeText(this@AddEventsActivity,"Please select image type", Toast.LENGTH_SHORT).show()
            }else if (binding.etDescription.text.toString().isNotEmpty()){
                addEventsViewModel.saveForEvent(taskId,binding.etDescription.text.toString(),binding.spinnerType.selectedItem.toString())
            }else{
                Toast.makeText(this@AddEventsActivity,"Please give comments", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subscribeObservers() {
        addEventsViewModel.myEventDataSTate.observe(this@AddEventsActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    Toast.makeText(this@AddEventsActivity,"Some error, Please try later", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    var eventList = ArrayList<String>()
                    eventList.add("Select an Event")
                    for (i in dataState.data.EventList.indices) {
                        eventList.add(dataState.data.EventList[i].TaskEvent)
                    }

                    val adapter = ArrayAdapter(this, R.layout.simple_spinner_item,eventList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerType.adapter = adapter
                }
            }.exhaustive
        }

        addEventsViewModel.mySetEventDataSTate.observe(this@AddEventsActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@AddEventsActivity,"Some error, Please try later", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    pd.dismiss()
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    val intent = Intent(this@AddEventsActivity, TaskActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }
            }.exhaustive
        }
    }
    override fun onResume() {
        super.onResume()
        addEventsViewModel.requestForEvent()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}