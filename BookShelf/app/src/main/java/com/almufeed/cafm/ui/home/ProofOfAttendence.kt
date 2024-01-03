package com.almufeed.cafm.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.almufeed.cafm.databinding.ActivityProofOfAttendenceBinding
import com.almufeed.cafm.ui.home.rateus.RatingActivity

class ProofOfAttendence : AppCompatActivity() {
    private lateinit var binding: ActivityProofOfAttendenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProofOfAttendenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var taskId = intent.getStringExtra("taskid").toString()
        binding.toolbar.aboutus.setText("Task : " + taskId)

        binding.btnSubmit.setOnClickListener(View.OnClickListener { view ->
            if(binding.nameInput.text.toString().isNotEmpty() && binding.mobileEditText.text.toString().isNotEmpty() && binding.emailInput.text.toString().isNotEmpty()){
                val intent = Intent(this@ProofOfAttendence, RatingActivity::class.java)
                intent.putExtra("taskid", taskId)
                intent.putExtra("name", binding.nameInput.text.toString())
                intent.putExtra("mobile", binding.mobileEditText.text.toString())
                intent.putExtra("email", binding.emailInput.text.toString())
                startActivity(intent)
            }else{
                Toast.makeText(this@ProofOfAttendence,"All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
           /* val intent = Intent(this@ProofOfAttendence, TaskDetailsActivity::class.java)
            intent.putExtra("taskid", taskId)
            intent.putExtra("status", "Add Instruction Steps")
            startActivity(intent)*/
        })
    }
}