package com.almufeed.cafm.ui.home.attachment

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.almufeed.cafm.R
import com.almufeed.cafm.databinding.ActivityCapturedPictureBinding
import com.almufeed.cafm.ui.base.BaseViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CapturedPictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCapturedPictureBinding
    private lateinit var taskId : String
    private lateinit var image1 : ByteArray
    private lateinit var image2 : ByteArray
    private lateinit var image3 : ByteArray
    private lateinit var type : String
    private lateinit var date : String
    private lateinit var description : String
    private val baseViewModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCapturedPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()
        taskId = intent.getStringExtra("taskid").toString()
        image1 = intent.getByteArrayExtra("image1")!!
        image2 = intent.getByteArrayExtra("image2")!!
        image3 = intent.getByteArrayExtra("image3")!!
        type = intent.getStringExtra("type").toString()
        date = intent.getStringExtra("date").toString()
        description = intent.getStringExtra("des").toString()

        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Task : $taskId"
        }
        binding.txtAttachtype.setText(type)
        binding.txtCurrentDateTime.setText(date)
        binding.etDescription.setText(description)
        Glide.with(binding.root.context)
            .load(image1)
            .into(binding.captureImage1)

        Glide.with(binding.root.context)
            .asBitmap().load(image2)
            .into(binding.captureImage2)

        Glide.with(binding.root.context)
            .asBitmap().load(image3)
            .into(binding.captureImage3)

    }
}