package com.almufeed.cafm.ui.home.attachment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.recyclerview.widget.GridLayoutManager
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
    private var convertedImage2 : String = ""
    private var convertedImage3 : String = ""
    private var convertedImage4 : String = ""
    private var convertedImage5 : String = ""
    private var convertedImage6 : String = ""
    private var convertedImage7 : String = ""
    private var convertedImage8 : String = ""
    private var convertedImage9 : String = ""
    private var convertedImage10 : String = ""
    private var convertedImage11 : String = ""
    private var convertedImage12 : String = ""
    private var convertedImage13 : String = ""
    private var convertedImage14 : String = ""
    private var convertedImage15 : String = ""

    private var convertedImageDB : ByteArray = "".toByteArray()
    private var convertedImageDB1 : ByteArray = "".toByteArray()
    private var convertedImageDB2 : ByteArray = "".toByteArray()
    private var convertedImageDB3 : ByteArray = "".toByteArray()
    private var convertedImageDB4 : ByteArray = "".toByteArray()
    private var convertedImageDB5 : ByteArray = "".toByteArray()
    private var convertedImageDB6 : ByteArray = "".toByteArray()
    private var convertedImageDB7 : ByteArray = "".toByteArray()
    private var convertedImageDB8 : ByteArray = "".toByteArray()
    private var convertedImageDB9 : ByteArray = "".toByteArray()
    private var convertedImageDB10 : ByteArray = "".toByteArray()
    private var convertedImageDB11 : ByteArray = "".toByteArray()
    private var convertedImageDB12 : ByteArray = "".toByteArray()
    private var convertedImageDB13 : ByteArray = "".toByteArray()
    private var convertedImageDB14 : ByteArray = "".toByteArray()
    private var convertedImageDB15 : ByteArray = "".toByteArray()

    private var Image1 : Boolean = false
    private var Image2 : Boolean = false
    private var Image3 : Boolean = false
    private var Image4 : Boolean = false
    private var Image5 : Boolean = false
    private var Image6 : Boolean = false
    private var Image7 : Boolean = false
    private var Image8 : Boolean = false
    private var Image9 : Boolean = false
    private var Image10 : Boolean = false
    private var Image11 : Boolean = false
    private var Image12 : Boolean = false
    private var Image13 : Boolean = false
    private var Image14 : Boolean = false
    private var Image15 : Boolean = false
    private var selectedImageType : Int = -1
    private lateinit var taskId : String
    private var selectedImage : String = ""
    private var fromEvent : Boolean = false
    private var fromTask : Boolean = false
    private lateinit var attachmentEntity : AttachmentEntity
    private lateinit var recyclerDataArrayList: ArrayList<Bitmap>
    private lateinit var courseRVAdapter: CourseRVAdapter

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
        fromTask = intent.getBooleanExtra("fromTask", false)

        if (fromEvent) {
            binding.idCourseRV.visibility = View.GONE
            binding.spinnerType.visibility = View.GONE
            binding.captureImage.visibility = View.VISIBLE
        } else {
            binding.idCourseRV.visibility = View.VISIBLE
            binding.spinnerType.visibility = View.VISIBLE
            binding.captureImage.visibility = View.GONE
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
            openCamera()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.btnSave.setOnClickListener {
            pd = Dialog(this, android.R.style.Theme_Black)
             val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
            pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
            pd.setContentView(view)
            pd.show()

            if (fromEvent) {
                if (isOnline(this@AddAttachmentActivity)) {
                    addEventsViewModel.saveForEvent(
                        taskId,
                        binding.etDescription.text.toString(),
                        selectedImage
                    )
                    addAttachmentViewModel.requestForImage(
                        convertedImage, "", "", "", "", "", "",
                        "", "", "", "", "", "", "", "",
                        5, binding.etDescription.text.toString(), taskId
                    )
                } else {
                    pd.dismiss()
                    val db = Room.databaseBuilder(
                        this@AddAttachmentActivity,
                        BookDatabase::class.java,
                        BookDatabase.DATABASE_NAME
                    ).allowMainThreadQueries().build()
                    lifecycleScope.launch {
                        attachmentEntity = addAttachmentViewModel.requestForImageDB(
                            convertedImageDB1,
                            convertedImageDB2,
                            convertedImageDB3,
                            convertedImageDB4,
                            convertedImageDB5,
                            convertedImageDB6,
                            selectedImageType,
                            binding.etDescription.text.toString(),
                            taskId
                        )
                    }
                    val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }
            } else {
                if (selectedImageType < 0) {
                    pd.dismiss()
                    Toast.makeText(this@AddAttachmentActivity, "Please select image type", Toast.LENGTH_SHORT).show()
                }else if(fromTask && binding.spinnerType.selectedItem.equals("Before") ){
                    pd.dismiss()
                    Toast.makeText(
                        this@AddAttachmentActivity,
                        "Select Image Type After Task",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (taskId.contains("RM") || binding.spinnerType.selectedItem.equals("Material Picture") || binding.spinnerType.selectedItem.equals(
                        "Inspection"
                    ) && convertedImage1.isNotEmpty() && convertedImage2.isNotEmpty() && convertedImage3.isNotEmpty()
                    && convertedImage4.isNotEmpty() && convertedImage5.isNotEmpty()
                ) {
                    pd.dismiss()
                    if (isOnline(this@AddAttachmentActivity)) {
                        if (binding.spinnerType.selectedItem.equals("Before")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "Before Task"
                            )
                        } else if (binding.spinnerType.selectedItem.equals("After")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "After Task"
                            )
                        } else if (binding.spinnerType.selectedItem.equals("Material Picture")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "Material Picture"
                            )
                        } else if (binding.spinnerType.selectedItem.equals("Inspection")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "Inspection"
                            )
                        }
                        addAttachmentViewModel.requestForImage(
                            convertedImage1,
                            convertedImage2,
                            convertedImage3,
                            convertedImage4,
                            convertedImage5,
                            convertedImage6,
                            convertedImage7,
                            convertedImage8,
                            convertedImage9,
                            convertedImage10,
                            convertedImage11,
                            convertedImage12,
                            convertedImage13,
                            convertedImage14,
                            convertedImage15,
                            selectedImageType,
                            binding.etDescription.text.toString(),
                            taskId
                        )
                    } else {
                        pd.dismiss()
                        val db = Room.databaseBuilder(
                            this@AddAttachmentActivity,
                            BookDatabase::class.java,
                            BookDatabase.DATABASE_NAME
                        ).allowMainThreadQueries().build()
                        lifecycleScope.launch {
                            attachmentEntity = addAttachmentViewModel.requestForImageDB(
                                convertedImageDB1,
                                convertedImageDB2,
                                convertedImageDB3,
                                convertedImageDB4,
                                convertedImageDB5,
                                convertedImageDB6,
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
                            db.bookDao()
                                .update("After Task", taskId, binding.etDescription.text.toString())
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
                        finish()
                    }
                }else if (taskId.contains("PPM") && convertedImage1.isNotEmpty() && convertedImage2.isNotEmpty() && convertedImage3.isNotEmpty()
                    && convertedImage4.isNotEmpty() && convertedImage5.isNotEmpty() && convertedImage6.isNotEmpty() && convertedImage7.isNotEmpty()
                    && convertedImage8.isNotEmpty() && convertedImage9.isNotEmpty() && convertedImage10.isNotEmpty()
                ) {
                    if (isOnline(this@AddAttachmentActivity)) {
                        if (binding.spinnerType.selectedItem.equals("Before")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "Before Task"
                            )
                        } else if (binding.spinnerType.selectedItem.equals("After")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "After Task"
                            )
                        } else if (binding.spinnerType.selectedItem.equals("Material Picture")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "Material Picture"
                            )
                        } else if (binding.spinnerType.selectedItem.equals("Inspection")) {
                            addEventsViewModel.saveForEvent(
                                taskId,
                                binding.etDescription.text.toString(),
                                "Inspection"
                            )
                        }
                        addAttachmentViewModel.requestForImage(
                            convertedImage1,
                            convertedImage2,
                            convertedImage3,
                            convertedImage4,
                            convertedImage5,
                            convertedImage6,
                            convertedImage7,
                            convertedImage8,
                            convertedImage9,
                            convertedImage10,
                            convertedImage11,
                            convertedImage12,
                            convertedImage13,
                            convertedImage14,
                            convertedImage15,
                            selectedImageType,
                            binding.etDescription.text.toString(),
                            taskId
                        )
                    } else {
                        pd.dismiss()
                        val db = Room.databaseBuilder(
                            this@AddAttachmentActivity,
                            BookDatabase::class.java,
                            BookDatabase.DATABASE_NAME
                        ).allowMainThreadQueries().build()
                        lifecycleScope.launch {
                            attachmentEntity = addAttachmentViewModel.requestForImageDB(
                                convertedImageDB1,
                                convertedImageDB2,
                                convertedImageDB3,
                                convertedImageDB4,
                                convertedImageDB5,
                                convertedImageDB6,
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
                            db.bookDao()
                                .update("After Task", taskId, binding.etDescription.text.toString())
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
                        finish()
                    }
                } else if (taskId.contains("RM") || binding.spinnerType.selectedItem.equals("Material Picture") || binding.spinnerType.selectedItem.equals(
                        "Inspection")){
                    Toast.makeText(
                        this@AddAttachmentActivity,
                        "Add minimum 5 images",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    pd.dismiss()
                    Toast.makeText(
                        this@AddAttachmentActivity,
                        "Add minimum 10 images",
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
        if(Image1){
            Image1 = false
            Image2 = true
        }else if(Image2){
            Image1 = false
            Image2 = false
            Image3 = true
        }else if(Image3){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = true
        }else if(Image4){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = true
        }else if(Image5){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = true
        }else if(Image6){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = true
        }else if(Image7){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = true
        }else if(Image8){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = true
        }else if(Image9){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = true
        }else if(Image10){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = true
        }else if(Image11){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = true
        }else if(Image12){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = false
            Image13 = true
        }else if(Image13){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = false
            Image13 = false
            Image14 = true
        }else if(Image14){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = false
            Image13 = false
            Image14 = false
            Image15 = true
        }else if(Image15){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = false
            Image13 = false
            Image14 = false
            Image15 = false
        }else{
            Image1 = true
        }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        if(Image1){
            Image1 = false
            Image2 = true
        }else if(Image2){
            Image1 = false
            Image2 = false
            Image3 = true
        }else if(Image3){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = true
        }else if(Image4){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = true
        }else if(Image5){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = true
        }else if(Image6){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = true
        }else if(Image7){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = true
        }else if(Image8){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = true
        }else if(Image9){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = true
        }else if(Image10){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = true
        }else if(Image11){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = true
        }else if(Image12){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = false
            Image13 = true
        }else if(Image13){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = false
            Image13 = false
            Image14 = true
        }else if(Image14){
            Image1 = false
            Image2 = false
            Image3 = false
            Image4 = false
            Image5 = false
            Image6 = false
            Image7 = false
            Image8 = false
            Image9 = false
            Image10 = false
            Image11 = false
            Image12 = false
            Image13 = false
            Image14 = false
            Image15 = true
        }else{
            Image1 = true
        }
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
                    val bitmap = data?.extras?.get("data") as Bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB = baos.toByteArray()
                    convertedImage = Base64.encodeToString(convertedImageDB, Base64.DEFAULT)
                    binding.captureImage.setImageBitmap(bitmap)
                } else {

                if (Image1) {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(0, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB1 = baos.toByteArray()
                    convertedImage1 = Base64.encodeToString(convertedImageDB1, Base64.DEFAULT)
                } else if (Image2) {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(1, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter


                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB2 = baos.toByteArray()
                    convertedImage2 = Base64.encodeToString(convertedImageDB2, Base64.DEFAULT)
                } else if (Image3) {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(2, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter


                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB3 = baos.toByteArray()
                    convertedImage3 = Base64.encodeToString(convertedImageDB3, Base64.DEFAULT)
                } else if (Image4) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(3, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter


                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB4 = baos.toByteArray()
                    convertedImage4 = Base64.encodeToString(convertedImageDB4, Base64.DEFAULT)
                } else if (Image5) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(4, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB5 = baos.toByteArray()
                    convertedImage5 = Base64.encodeToString(convertedImageDB5, Base64.DEFAULT)

                } else if (Image6) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(5, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB6 = baos.toByteArray()
                    convertedImage6 = Base64.encodeToString(convertedImageDB6, Base64.DEFAULT)
                } else if (Image7) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(6, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB7 = baos.toByteArray()
                    convertedImage7 = Base64.encodeToString(convertedImageDB7, Base64.DEFAULT)
                } else if (Image8) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(7, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB8 = baos.toByteArray()
                    convertedImage8 = Base64.encodeToString(convertedImageDB8, Base64.DEFAULT)
                } else if (Image9) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(8, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB9 = baos.toByteArray()
                    convertedImage9 = Base64.encodeToString(convertedImageDB9, Base64.DEFAULT)
                } else if (Image10) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(9, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB10 = baos.toByteArray()
                    convertedImage10 = Base64.encodeToString(convertedImageDB10, Base64.DEFAULT)
                } else if (Image11) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(10, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB11 = baos.toByteArray()
                    convertedImage11 = Base64.encodeToString(convertedImageDB11, Base64.DEFAULT)
                } else if (Image12) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(11, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB12 = baos.toByteArray()
                    convertedImage12 = Base64.encodeToString(convertedImageDB12, Base64.DEFAULT)
                } else if (Image13) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(12, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB13 = baos.toByteArray()
                    convertedImage13 = Base64.encodeToString(convertedImageDB13, Base64.DEFAULT)
                } else if (Image14) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(13, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB14 = baos.toByteArray()
                    convertedImage14 = Base64.encodeToString(convertedImageDB14, Base64.DEFAULT)
                } else if (Image15) {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    recyclerDataArrayList.add(14, bitmap)
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.idCourseRV.layoutManager = layoutManager
                    courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                    binding.idCourseRV.adapter = courseRVAdapter

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB15 = baos.toByteArray()
                    convertedImage15 = Base64.encodeToString(convertedImageDB15, Base64.DEFAULT)
                }
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
                    if (Image1) {
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
                        recyclerDataArrayList.add(0, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos1) // bm is the bitmap object
                        convertedImageDB1 = baos1.toByteArray()
                        convertedImage1 = Base64.encodeToString(convertedImageDB1, Base64.DEFAULT)

                    } else if (Image2) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(1, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB2 = baos1.toByteArray()
                        convertedImage2 = Base64.encodeToString(convertedImageDB2, Base64.DEFAULT)
                    } else if (Image3) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(2, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB3 = baos1.toByteArray()
                        convertedImage3 = Base64.encodeToString(convertedImageDB3, Base64.DEFAULT)
                    } else if (Image4) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(3, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB4 = baos1.toByteArray()
                        convertedImage4 = Base64.encodeToString(convertedImageDB4, Base64.DEFAULT)
                    } else if (Image5) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(4, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB5 = baos1.toByteArray()
                        convertedImage5 = Base64.encodeToString(convertedImageDB5, Base64.DEFAULT)
                    } else if (Image6) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(5, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB6 = baos1.toByteArray()
                        convertedImage6 = Base64.encodeToString(convertedImageDB6, Base64.DEFAULT)
                    } else if (Image7) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(6, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB7 = baos1.toByteArray()
                        convertedImage7 = Base64.encodeToString(convertedImageDB7, Base64.DEFAULT)
                    } else if (Image8) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(7, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB8 = baos1.toByteArray()
                        convertedImage8 = Base64.encodeToString(convertedImageDB8, Base64.DEFAULT)
                    } else if (Image9) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(8, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB9 = baos1.toByteArray()
                        convertedImage9 = Base64.encodeToString(convertedImageDB9, Base64.DEFAULT)
                    } else if (Image10) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(9, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB10 = baos1.toByteArray()
                        convertedImage10 = Base64.encodeToString(convertedImageDB10, Base64.DEFAULT)
                    } else if (Image11) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(10, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB11 = baos1.toByteArray()
                        convertedImage11 = Base64.encodeToString(convertedImageDB11, Base64.DEFAULT)
                    } else if (Image12) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(11, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB12 = baos1.toByteArray()
                        convertedImage12 = Base64.encodeToString(convertedImageDB12, Base64.DEFAULT)
                    } else if (Image13) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(12, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB13 = baos1.toByteArray()
                        convertedImage13 = Base64.encodeToString(convertedImageDB13, Base64.DEFAULT)
                    } else if (Image14) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(13, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB14 = baos1.toByteArray()
                        convertedImage14 = Base64.encodeToString(convertedImageDB14, Base64.DEFAULT)
                    } else if (Image15) {
                        val uri = data?.getData()
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        recyclerDataArrayList.add(14, bitmap)
                        val layoutManager = GridLayoutManager(this, 3)
                        binding.idCourseRV.layoutManager = layoutManager
                        courseRVAdapter = CourseRVAdapter(recyclerDataArrayList)
                        binding.idCourseRV.adapter = courseRVAdapter

                        val baos1 = ByteArrayOutputStream()
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            baos1
                        ) // bm is the bitmap object
                        convertedImageDB15 = baos1.toByteArray()
                        convertedImage15 = Base64.encodeToString(convertedImageDB15, Base64.DEFAULT)
                    }
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
                        val intent = Intent(this@AddAttachmentActivity, TaskActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
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