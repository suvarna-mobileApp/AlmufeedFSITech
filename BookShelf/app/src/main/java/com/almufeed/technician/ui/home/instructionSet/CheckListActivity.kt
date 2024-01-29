package com.almufeed.technician.ui.home.instructionSet

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
import com.almufeed.technician.R
import com.almufeed.technician.business.domain.state.DataState
import com.almufeed.technician.business.domain.utils.exhaustive
import com.almufeed.technician.business.domain.utils.isOnline
import com.almufeed.technician.databinding.ActivityCheckListBinding
import com.almufeed.technician.datasource.cache.database.BookDatabase
import com.almufeed.technician.datasource.cache.database.BookDatabase.Companion.DATABASE_NAME
import com.almufeed.technician.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.technician.datasource.network.models.instructionSet.InstructionData
import com.almufeed.technician.ui.home.TaskDetailsActivity
import com.almufeed.technician.ui.home.attachment.AttachmentList
import com.almufeed.technician.ui.home.events.AddEventsActivity
import com.almufeed.technician.ui.home.events.AddEventsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_task.recyclerTask

@AndroidEntryPoint
class CheckListActivity : AppCompatActivity(),InstructionRecyclerAdapter.OnItemClickListener,InstructionRecyclerAdapterOffline.OnItemClickListener{
    private lateinit var binding: ActivityCheckListBinding
    private val checkListViewModel: CheckListViewModel by viewModels()
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private lateinit var instructionRecyclerAdapter: InstructionRecyclerAdapter
    private lateinit var instructionRecyclerAdapterOffline: InstructionRecyclerAdapterOffline
    private lateinit var instructionList: ArrayList<InstructionData>
    private lateinit var instructionListOffline: ArrayList<GetInstructionSetEntity>
    private lateinit var taskId : String
    private lateinit var pd : Dialog
    private lateinit var db :BookDatabase

    companion object {
        var clickedButtonCount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()
        taskId = intent.getStringExtra("taskid").toString()
        db = Room.databaseBuilder(this, BookDatabase::class.java, DATABASE_NAME).allowMainThreadQueries().build()

        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.aboutus.text = "Task : $taskId"
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.incToolbarEvent.visibility = View.VISIBLE
        binding.toolbar.incToolbarAttachment.visibility = View.VISIBLE
        binding.toolbar.incToolbarEvent.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@CheckListActivity, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
        })

        binding.toolbar.incToolbarAttachment.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@CheckListActivity, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
        })

        binding.btnAccept.setOnClickListener(View.OnClickListener { view ->
            if(isOnline(this@CheckListActivity)){
                val viewHolder = instructionRecyclerAdapter.createViewHolder(binding.recyclerTask, 0)
                instructionRecyclerAdapter.bindViewHolder(viewHolder, 0)

                val allButtonsClicked = clickedButtonCount >= instructionList.size
                if (allButtonsClicked) {
                    pd.show()
                    addEventsViewModel.saveForEvent(taskId,"","Instruction set completed")
                }else{
                    System.out.println("suvarna pressed no " + clickedButtonCount)
                    Toast.makeText(this@CheckListActivity,"All Instruction set are mandatory", Toast.LENGTH_SHORT).show()
                }
            }else{
                val viewHolder = instructionRecyclerAdapterOffline.createViewHolder(binding.recyclerTask, 0)
                instructionRecyclerAdapterOffline.bindViewHolder(viewHolder, 0)
                val allButtonsClicked = clickedButtonCount >= instructionListOffline.size
                if (allButtonsClicked) {
                    db.bookDao().update("Instruction set completed",taskId,"Instruction set completed")
                    db.bookDao().updateLOC("Instruction set completed",taskId)
                    val intent = Intent(this@CheckListActivity, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }else{
                    System.out.println("suvarna pressed no " + clickedButtonCount)
                    Toast.makeText(this@CheckListActivity,"All Instruction set are mandatory", Toast.LENGTH_SHORT).show()
                }
            }
        })


        if(isOnline(this@CheckListActivity)){
            pd = Dialog(this, android.R.style.Theme_Black)
            val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
            pd.getWindow()!!.setBackgroundDrawableResource(com.almufeed.technician.R.color.transparent)
            pd.setContentView(view)
            pd.show()

            checkListViewModel.requestForStep(taskId)
            subscribeObservers()
            subscribeObserversUpdate()
        }else{
            //Toast.makeText(this@CheckListActivity,"No Internet Connection", Toast.LENGTH_SHORT).show()
            val instructionSet = db.bookDao().AllInstructionSet(taskId)
            System.out.println("instruction set " + instructionSet)
            if(instructionSet.size > 0){
                instructionListOffline = ArrayList<GetInstructionSetEntity>()
                for(i in instructionSet.indices){
                    when(instructionSet[i].FeedbackType) {
                        0 -> {

                        }

                        1 -> {
                            instructionListOffline.add(instructionSet[i])
                        }

                        2 -> {

                        }

                        3 -> {
                            instructionListOffline.add(instructionSet[i])
                        }
                        else -> print("I don't know anything about it")
                    }
                }
                 binding.recyclerTask.apply {
                     instructionRecyclerAdapterOffline = InstructionRecyclerAdapterOffline(instructionSet,this@CheckListActivity,this@CheckListActivity)
                     layoutManager = LinearLayoutManager(this@CheckListActivity)
                     recyclerTask.adapter = instructionRecyclerAdapterOffline
                 }
            }else{
                val intent = Intent(this@CheckListActivity, TaskDetailsActivity::class.java)
                intent.putExtra("taskid", taskId)
                intent.putExtra("fromTaskBefore", true)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun subscribeObservers() {
        checkListViewModel.myTaskDataSTate.observe(this@CheckListActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@CheckListActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    if(dataState.data.problem.size > 0){
                        val instructionSet = db.bookDao().AllInstructionSet(taskId)
                        instructionList = ArrayList<InstructionData>()
                        binding.recyclerTask.apply {
                            instructionRecyclerAdapter = InstructionRecyclerAdapter(dataState.data,this@CheckListActivity,this@CheckListActivity)
                            layoutManager = LinearLayoutManager(this@CheckListActivity)
                            recyclerTask.adapter = instructionRecyclerAdapter
                        }
                        for(i in dataState.data.problem.indices){
                            when(dataState.data.problem[i].FeedbackType) {
                                0 -> {

                                }

                                1 -> {
                                    instructionList.add(dataState.data.problem[i])
                                }

                                2 -> {

                                }

                                3 -> {
                                    instructionList.add(dataState.data.problem[i])
                                }
                                else -> print("I don't know anything about it")
                            }
                        }
                        /*binding.recyclerTask.apply {
                            instructionRecyclerAdapter = InstructionRecyclerAdapter(instructionSet,this@CheckListActivity,this@CheckListActivity)
                            layoutManager = LinearLayoutManager(this@CheckListActivity)
                            recyclerTask.adapter = instructionRecyclerAdapter
                        }*/
                    }else{
                        val intent = Intent(this@CheckListActivity, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }
                }

                else -> {}
            }.exhaustive
        }

        addEventsViewModel.mySetEventDataSTate.observe(this@CheckListActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    dataState.exception.message
                    Toast.makeText(this@CheckListActivity,dataState.exception.message, Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    if(dataState.data.Success){
                        val intent = Intent(this@CheckListActivity, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@CheckListActivity,"Some error, please try later", Toast.LENGTH_SHORT).show()
                    }
                }
            }.exhaustive
        }
    }

    private fun subscribeObserversUpdate() {
        checkListViewModel.myUpdateDataSTate.observe(this@CheckListActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@CheckListActivity,"Some error, please try later", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    pd.dismiss()
                }
                else -> {}
            }.exhaustive
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(refId: Long, feedBackType: Int, answer: String,position: Int) {

        if(isOnline(this@CheckListActivity)){
            pd.show()
            checkListViewModel.updateForStep(refId,feedBackType,answer,taskId)
        }else{
            val refId1 = db.bookDao().getRefId(refId,taskId)
            if(refId1 == refId){
                db.bookDao().updateSet(answer,refId)
                System.out.println("suvarna print update ")
            }else{
                System.out.println("suvarna print insert ")
                val taskRequest = InstructionSetEntity(
                    id = 0,
                    Refrecid = refId,
                    FeedbackType = feedBackType,
                    AnswerSet = answer,
                    taskId = taskId
                )
                db.bookDao().insertSet(taskRequest)
            }
        }
    }
}