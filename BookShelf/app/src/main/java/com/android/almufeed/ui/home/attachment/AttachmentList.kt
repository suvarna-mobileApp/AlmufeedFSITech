package com.android.almufeed.ui.home.attachment

import android.app.Dialog
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
import com.android.almufeed.databinding.ActivityAttachmentListBinding
import com.android.almufeed.datasource.cache.database.BookDatabase
import com.android.almufeed.datasource.cache.models.offlineDB.AttachmentEntity
import com.android.almufeed.ui.base.BaseViewModel
import com.android.almufeed.ui.home.TaskDetailsActivity
import com.android.almufeed.ui.home.events.AddEventsActivity
import com.android.almufeed.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttachmentList : AppCompatActivity() {
    private lateinit var binding: ActivityAttachmentListBinding
    private lateinit var taskId : String
    private lateinit var attachmentRecyclerAdapter : AttachmentRecyclerAdapter
    private val viewModel: GetAttachmentViewModel by viewModels()
    private lateinit var pd : Dialog
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var attachmentEntity : AttachmentEntity
    private lateinit var db:BookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttachmentListBinding.inflate(layoutInflater)
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
        binding.toolbar.incToolbarEvent.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@AttachmentList, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        /*binding.toolbar.incToolbarAttachment.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@AttachmentList, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })*/

        binding.btnSave.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@AttachmentList, AddAttachmentActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        db = Room.databaseBuilder(this@AttachmentList, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()
        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.show()
        if(isOnline(this@AttachmentList)){
            viewModel.requestForImage(taskId)
            subscribeObservers()
        }else{
            val attachmentList = db.bookDao().getAllAttachment(taskId)
            pd.dismiss()
            if(attachmentList.size > 0){
                binding.recyclerAttach.visibility = View.VISIBLE
                binding.noDataFoundTv.visibility = View.GONE
             /*   binding.recyclerAttach.apply {
                    attachmentRecyclerAdapter = AttachmentRecyclerAdapter(attachmentList,this@AttachmentList)
                    layoutManager = LinearLayoutManager(this@AttachmentList)
                    binding.recyclerAttach.adapter = attachmentRecyclerAdapter
                }*/
            }else{
                binding.recyclerAttach.visibility = View.GONE
                binding.noDataFoundTv.visibility = View.VISIBLE
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.myImageDataSTate.observe(this@AttachmentList) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    dataState.exception.message
                    if(dataState.exception.message.equals("Authentication failed")){
                        Toast.makeText(this@AttachmentList,dataState.exception.message, Toast.LENGTH_SHORT).show()
                        baseViewModel.setToken("")
                        baseViewModel.updateUsername("")
                        Intent(this@AttachmentList, LoginActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    }else{
                        Toast.makeText(this@AttachmentList,dataState.exception.message, Toast.LENGTH_SHORT).show()
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
                    if(dataState.data.fsiImage.isEmpty()){
                        binding.recyclerAttach.visibility = View.GONE
                        binding.noDataFoundTv.visibility = View.VISIBLE
                    }else{
                        binding.recyclerAttach.visibility = View.VISIBLE
                        binding.noDataFoundTv.visibility = View.GONE
                        binding.recyclerAttach.apply {
                            attachmentRecyclerAdapter = AttachmentRecyclerAdapter(dataState.data,this@AttachmentList)
                            layoutManager = LinearLayoutManager(this@AttachmentList)
                            binding.recyclerAttach.adapter = attachmentRecyclerAdapter
                        }
                    }
                }
            }.exhaustive
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}