package com.almufeed.technician.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almufeed.technician.business.domain.utils.dateFormater
import com.almufeed.technician.databinding.RecyclerTaskadapterBinding
import com.almufeed.technician.datasource.network.models.bookList.BookData
import com.almufeed.technician.datasource.network.models.tasklist.TaskListResponse
import com.almufeed.technician.ui.home.TaskDetailsActivity

class TaskRecyclerAdapter (
    val taskList: TaskListResponse, val context: Context
) : RecyclerView.Adapter<TaskRecyclerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RecyclerTaskadapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    fun filterList(filterList: ArrayList<BookData>) {
        // below line is to add our filtered
        // list in our course array list.
        taskList.task = filterList
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = taskList.task.get(position)
        if (currentItem != null) {
            holder.bind(currentItem,position)
        }
    }

    inner class ItemViewHolder(private val binding: RecyclerTaskadapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: BookData, position: Int) {
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
                txtproblem.text = "Problem :  " + currentItem.Problem
                txtpriority.text = "Priority :  " + currentItem.Priority
                txtcategory.text = "Category :  " + currentItem.Category
                txtStatus.text = "Task Status :  " + currentItem.LOC
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.task.size
    }
}