package com.android.almufeed.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.almufeed.R
import com.android.almufeed.business.domain.utils.dateFormater
import com.android.almufeed.business.domain.utils.formatQcDate
import com.android.almufeed.business.domain.utils.getDate
import com.android.almufeed.databinding.RecyclerTaskadapterBinding
import com.android.almufeed.datasource.cache.database.BookDatabase
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity
import com.android.almufeed.datasource.network.models.bookList.BookData
import com.android.almufeed.datasource.network.models.bookList.BookListNetworkResponse
import com.android.almufeed.datasource.network.models.tasklist.TaskListResponse
import com.android.almufeed.ui.home.RiskAssessment
import com.android.almufeed.ui.home.TaskDetailsActivity

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
                txtscheduledate.text = "Reported Date :  " + dateFormater(currentItem.scheduledDate)
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