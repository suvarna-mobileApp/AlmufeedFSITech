package com.almufeed.cafm.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almufeed.cafm.business.domain.utils.dateFormater
import com.almufeed.cafm.databinding.RecyclerTaskadapterBinding
import com.almufeed.cafm.datasource.cache.models.offlineDB.TaskEntity
import com.almufeed.cafm.ui.home.TaskDetailsActivity

class TaskRecyclerAdapter (val taskList: List<TaskEntity>, val context: Context
) : RecyclerView.Adapter<TaskRecyclerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RecyclerTaskadapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = taskList.get(position)
        if (currentItem != null) {
            holder.bind(currentItem,position)
        }
    }

    inner class ItemViewHolder(private val binding: RecyclerTaskadapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: TaskEntity,position: Int) {
            binding.apply {
                itemView.setOnClickListener {
                    val intent = Intent(context, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", currentItem.TaskId)
                    intent.putExtra("description", currentItem.Notes)
                    intent.putExtra("priority", currentItem.Priority)
                    intent.putExtra("scheduledate", currentItem.scheduledDate)
                    intent.putExtra("duedate", currentItem.attendDate)
                    intent.putExtra("cusname", currentItem.CustName)
                    intent.putExtra("mobile", currentItem.Phone)
                    intent.putExtra("building", currentItem.Building)
                    intent.putExtra("location", currentItem.Location)
                    intent.putExtra("status", currentItem.LOC)
                    context.startActivity(intent)
                }

                if(currentItem.hazard){
                    vip.visibility = View.VISIBLE
                }else{
                    vip.visibility = View.GONE
                }

                /*if(position == 0){
                    //rel.setBackgroundResource(R.drawable.border)
                    vip.visibility = View.VISIBLE
                }*/

                txtTaskid.text = "Task ID - " + currentItem.TaskId
                txtscheduledate.text = "Scheduled Date :  " + dateFormater(currentItem.scheduledDate)
                txtAttendDate.text = "Due Date :  " + dateFormater(currentItem.attendDate)
                txtbuilding.text = "Building :  " + currentItem.Building
                txtlocation.text = "Location :  " + currentItem.Location
                txtpriority.text = "Priority :  " + currentItem.Priority
                txtproblem.text = "Problem :  " + currentItem.Problem
                txtcategory.text = "Category :  " + currentItem.Category
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}