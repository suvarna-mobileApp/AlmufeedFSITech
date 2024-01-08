package com.almufeed.cafm.ui.home.attachment

import android.Manifest
import android.R.attr.bitmap
import android.R.attr.x
import android.R.attr.y
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.almufeed.cafm.R
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.domain.utils.isOnline
import com.almufeed.cafm.databinding.ActivityAddAttachmentBinding
import com.almufeed.cafm.datasource.cache.database.BookDatabase
import com.almufeed.cafm.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.cafm.ui.home.TaskActivity
import com.almufeed.cafm.ui.home.TaskDetailsActivity
import com.almufeed.cafm.ui.home.TaskDetailsActivity.Companion.clickedButtonCountAfter
import com.almufeed.cafm.ui.home.TaskDetailsActivity.Companion.clickedButtonCountBefore
import com.almufeed.cafm.ui.home.events.AddEventsActivity
import com.almufeed.cafm.ui.home.events.AddEventsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date


@AndroidEntryPoint
class AddAttachmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAttachmentBinding
    private val addAttachmentViewModel: AddAttachmentViewModel by viewModels()
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    var imageType = arrayOf("Select an image type", "Before", "After","Material Picture","Inspection")
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private lateinit var pd : Dialog
    private var convertedImage : String = ""
    private var convertedImage1 : String = ""

    private var convertedImageDB : ByteArray = "".toByteArray()
    private var convertedImageDB1 : ByteArray = "".toByteArray()

    private var selectedImageType : Int = -1
    private lateinit var taskId : String
    private var selectedImage : String = ""
    private var fromEvent : Boolean = false
    private lateinit var attachmentEntity : AttachmentEntity
    private lateinit var recyclerDataArrayList: ArrayList<Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAttachmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerDataArrayList = ArrayList()
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

        selectedImage = intent.getStringExtra("selectedImage").toString()
        fromEvent = intent.getBooleanExtra("fromEvent", false)

        if (fromEvent) {
            binding.spinnerType.visibility = View.GONE
        } else {
            binding.spinnerType.visibility = View.VISIBLE
        }

        binding.toolbar.linTool.visibility = View.VISIBLE
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val currentDate = sdf.format(Date())
        binding.txtCurrentDateTime.setText(currentDate)

        binding.toolbar.incToolbarEvent.setOnClickListener(View.OnClickListener { view ->
            val intent = Intent(this@AddAttachmentActivity, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        binding.toolbar.incToolbarAttachment.setOnClickListener(View.OnClickListener { view ->
            val intent = Intent(this@AddAttachmentActivity, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, imageType)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter

        binding.spinnerType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                /* type 0 = before
                type 1 = after
                type 2 = material
                type 3 = inspection*/
                selectedImageType = position - 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.btnImage.setOnClickListener {
            if(recyclerDataArrayList.size < 15){
                openCamera()
            }else{
                Toast.makeText(this@AddAttachmentActivity, "Limit is 15 images", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGallery.setOnClickListener {
            if(recyclerDataArrayList.size < 15){
                openGallery()
            }else{
                Toast.makeText(this@AddAttachmentActivity, "Limit is 15 images", Toast.LENGTH_SHORT).show()
            }
        }

       /* binding.btnSave.setOnClickListener {
            if(binding.spinnerType.selectedItem.equals("Before")){
                if(clickedButtonCountBefore < 15){
                    clickedButtonCountBefore++
                    System.out.println("suvarna before " + clickedButtonCountBefore)
                }
            }else if(binding.spinnerType.selectedItem.equals("After")){
                if(clickedButtonCountAfter < 15){
                    clickedButtonCountAfter++
                }
            }
            val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
        }*/


        binding.btnSave.setOnClickListener {
            pd = Dialog(this, android.R.style.Theme_Black)
             val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
            pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
            pd.setContentView(view)

            if (fromEvent) {
                if (isOnline(this@AddAttachmentActivity)) {
                    pd.show()
                    addAttachmentViewModel.requestForImage(
                        convertedImage,
                        5, binding.etDescription.text.toString(), taskId
                    )
                } else {
                    Toast.makeText(
                        this@AddAttachmentActivity,
                        "No Internet Connection",
                        Toast.LENGTH_SHORT
                    ).show()
                   /* pd.dismiss()
                    val db = Room.databaseBuilder(
                        this@AddAttachmentActivity,
                        BookDatabase::class.java,
                        BookDatabase.DATABASE_NAME
                    ).allowMainThreadQueries().build()
                    lifecycleScope.launch {
                        attachmentEntity = addAttachmentViewModel.requestForImageDB(
                            convertedImageDB1,
                            selectedImageType,
                            binding.etDescription.text.toString(),
                            taskId
                        )
                    }
                    val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()*/
                }
            } else {
                if (selectedImageType < 0) {
                    Toast.makeText(this@AddAttachmentActivity, "Select Image Type", Toast.LENGTH_SHORT).show()
                }else if (clickedButtonCountBefore > 15) {
                    Toast.makeText(this@AddAttachmentActivity, "Select Image Type After", Toast.LENGTH_SHORT).show()
                }else if (convertedImage1.isNotEmpty()) {
                    if (isOnline(this@AddAttachmentActivity)) {
                        pd.show()
                        if (binding.spinnerType.selectedItem.equals("Before")) {
                            addEventsViewModel.saveForEvent(taskId, binding.etDescription.text.toString(), "Before Task")
                        } else if (binding.spinnerType.selectedItem.equals("After")) {
                            addEventsViewModel.saveForEvent(taskId, binding.etDescription.text.toString(), "After Task")
                        } else if (binding.spinnerType.selectedItem.equals("Material Picture")) {
                            addEventsViewModel.saveForEvent(taskId, binding.etDescription.text.toString(), "Material Picture")
                        } else if (binding.spinnerType.selectedItem.equals("Inspection")) {
                            addEventsViewModel.saveForEvent(taskId, binding.etDescription.text.toString(), "Inspection")
                        }
                        addAttachmentViewModel.requestForImage(
                            convertedImage1,
                            selectedImageType,
                            binding.etDescription.text.toString(),
                            taskId
                        )
                    } else {

                        Toast.makeText(
                            this@AddAttachmentActivity,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
              /*          pd.dismiss()
                        val db = Room.databaseBuilder(
                            this@AddAttachmentActivity,
                            BookDatabase::class.java,
                            BookDatabase.DATABASE_NAME
                        ).allowMainThreadQueries().build()
                        lifecycleScope.launch {
                            attachmentEntity = addAttachmentViewModel.requestForImageDB(
                                convertedImageDB1,
                                selectedImageType,
                                binding.etDescription.text.toString(),
                                taskId
                            )
                        }
                        if (binding.spinnerType.selectedItem.equals("Before")) {
                            db.bookDao().insertAddAttachmentSet(attachmentEntity)
                            db.bookDao()
                                .update(
                                    "Before Task",
                                    taskId,
                                    binding.etDescription.text.toString()
                                )
                        } else if (binding.spinnerType.selectedItem.equals("After")) {
                            db.bookDao().insertAddAttachmentSet(attachmentEntity)
                            db.bookDao().update("After Task", taskId, binding.etDescription.text.toString())
                        } else if (binding.spinnerType.selectedItem.equals("Material Picture")) {
                            db.bookDao().insertAddAttachmentSet(attachmentEntity)
                            db.bookDao().update(
                                "Material Picture",
                                taskId,
                                binding.etDescription.text.toString()
                            )
                        } else if (binding.spinnerType.selectedItem.equals("Inspection")) {
                            db.bookDao().insertAddAttachmentSet(attachmentEntity)
                            db.bookDao()
                                .update("Inspection", taskId, binding.etDescription.text.toString())
                        }
                        val intent =
                            Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()*/
                    }
                } else {
                    pd.dismiss()
                    Toast.makeText(
                        this@AddAttachmentActivity,
                        "Add images to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (fromEvent) {
                    val x = 5f // X-coordinate where you want to draw the text
                    val y = 10f
                    val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm")
                    val currentDate = sdf.format(Date())
                    val bitmap = data?.extras?.get("data") as Bitmap
                    val canvas = Canvas(bitmap)
                    val paint = Paint()
                    paint.setColor(Color.RED)
                    paint.setTextSize(10f)
                    canvas.drawText("Date and Time " + currentDate, x, y, paint)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB = baos.toByteArray()
                    convertedImage = Base64.encodeToString(convertedImageDB, Base64.DEFAULT)
                    binding.captureImage.setImageBitmap(bitmap)
                } else {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.captureImage.setImageBitmap(bitmap)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB1 = baos.toByteArray()
                    convertedImage1 = Base64.encodeToString(convertedImageDB1, Base64.DEFAULT)
            }
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                if (fromEvent) {
                    val uri = data?.getData()
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        80,
                        baos1
                    ) // bm is the bitmap object
                    convertedImageDB = baos1.toByteArray()
                    convertedImage = Base64.encodeToString(convertedImageDB, Base64.DEFAULT)
                    binding.captureImage.setImageBitmap(bitmap)
                }else {

                        val uri = data?.getData()
                        /*val iv = ImageView(this)
                    val param: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        400, 800
                    )
                    param.setMargins(0, 0, 2, 0)
                    iv.setLayoutParams(param)
                    iv.setImageURI(uri)
                    iv.setAdjustViewBounds(true)
                    binding.linImage.addView(iv)*/

                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        binding.captureImage.setImageBitmap(bitmap)
                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos1) // bm is the bitmap object
                        convertedImageDB1 = baos1.toByteArray()
                        convertedImage1 = Base64.encodeToString(convertedImageDB1, Base64.DEFAULT)
                }
            }
        }
    }

    private fun subscribeObservers() {
        addAttachmentViewModel.myImageDataSTate.observe(this@AddAttachmentActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    Toast.makeText(this@AddAttachmentActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    if(fromEvent){
                        addEventsViewModel.saveForEvent(
                            taskId,
                            binding.etDescription.text.toString(),
                            selectedImage
                        )
                        val intent = Intent(this@AddAttachmentActivity, TaskActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        if(binding.spinnerType.selectedItem.equals("Before")){
                            if(clickedButtonCountBefore < 15){
                                clickedButtonCountBefore++
                                System.out.println("suvarna before " + clickedButtonCountBefore)
                            }
                        }else if(binding.spinnerType.selectedItem.equals("After")){
                            if(clickedButtonCountAfter < 15){
                                clickedButtonCountAfter++
                            }
                        }
                        val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
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