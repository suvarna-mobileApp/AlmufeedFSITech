package com.android.almufeed.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.almufeed.databinding.ActivityProofOfAttendenceBinding
import com.android.almufeed.ui.home.instructionSet.CheckListActivity

class ProofOfAttendence : AppCompatActivity() {
    private lateinit var binding: ActivityProofOfAttendenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProofOfAttendenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var taskId = intent.getStringExtra("taskid").toString()
        binding.toolbar.aboutus.setText("Task : " + taskId)

        binding.btnNext.setOnClickListener(View.OnClickListener { view ->

            val intent = Intent(this@ProofOfAttendence, CheckListActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
           /* val intent = Intent(this@ProofOfAttendence, TaskDetailsActivity::class.java)
            intent.putExtra("taskid", taskId)
            intent.putExtra("status", "Add Instruction Steps")
            startActivity(intent)*/
        })
    }
}