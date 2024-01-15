package com.almufeed.cafm.ui.home.attachment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
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
import com.almufeed.cafm.business.domain.utils.clickedButtonCountAfter
import com.almufeed.cafm.business.domain.utils.clickedButtonCountBefore
import com.almufeed.cafm.business.domain.utils.clickedButtonCountInspection
import com.almufeed.cafm.business.domain.utils.clickedButtonCountMaterialPicture
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.domain.utils.isOnline
import com.almufeed.cafm.databinding.ActivityAddAttachmentBinding
import com.almufeed.cafm.datasource.cache.database.BookDatabase
import com.almufeed.cafm.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.cafm.ui.base.BaseViewModel
import com.almufeed.cafm.ui.home.TaskActivity
import com.almufeed.cafm.ui.home.TaskDetailsActivity
import com.almufeed.cafm.ui.home.events.AddEventsActivity
import com.almufeed.cafm.ui.home.events.AddEventsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class AddAttachmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAttachmentBinding
    private val addAttachmentViewModel: AddAttachmentViewModel by viewModels()
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    var imageType = arrayOf("Select an image type", "Before", "After","Material Picture","Inspection")
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private lateinit var pd : Dialog
    private var convertedImageNoAccess : String = ""
    private var convertedImage1 : String = ""

    private var convertedImageDBNoAccess : ByteArray = "".toByteArray()
    private var convertedImageDB1 : ByteArray = "".toByteArray()

    private var selectedImageType : Int = -1
    private lateinit var taskId : String
    private lateinit var db : BookDatabase
    private var selectedImageNoAccess : String = ""
    private var fromEvent : Boolean = false
    private lateinit var attachmentEntity : AttachmentEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAttachmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        taskId = intent.getStringExtra("taskid").toString()
        selectedImageNoAccess = intent.getStringExtra("selectedImage").toString()
        fromEvent = intent.getBooleanExtra("fromEvent", false)

        db = Room.databaseBuilder(this@AddAttachmentActivity, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()
        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.aboutus.text = "Task : $taskId"
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.linTool.visibility = View.VISIBLE
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val currentDate = sdf.format(Date())
        binding.txtCurrentDateTime.setText(currentDate)

        binding.toolbar.incToolbarEvent.setOnClickListener{
            val intent = Intent(this@AddAttachmentActivity, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        }

        binding.toolbar.incToolbarAttachment.setOnClickListener{
            val intent = Intent(this@AddAttachmentActivity, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        }

        if (fromEvent) {
            binding.spinnerType.visibility = View.GONE
        } else {
            binding.spinnerType.visibility = View.VISIBLE
        }

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

        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)

        binding.btnImage.setOnClickListener {
            openCamera()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.btnSave.setOnClickListener {
            if (fromEvent) {
                if (isOnline(this@AddAttachmentActivity)) {
                    pd.show()
                    addAttachmentViewModel.requestForImage(
                        convertedImageNoAccess,
                        5, binding.etDescription.text.toString(), taskId
                    )
                } else {
                    //Toast.makeText(this@AddAttachmentActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()

                    lifecycleScope.launch {
                        attachmentEntity = addAttachmentViewModel.requestForImageDB(
                            convertedImageNoAccess,
                            5,
                            binding.etDescription.text.toString(),
                            taskId
                        )
                        db.bookDao().insertAddAttachmentSet(attachmentEntity)
                    }
                    val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }
            } else {
                if (selectedImageType < 0) {
                    Toast.makeText(this@AddAttachmentActivity, "Select Image Type", Toast.LENGTH_SHORT).show()
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
                        //Toast.makeText(this@AddAttachmentActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
                        pd.dismiss()
                        lifecycleScope.launch {
                            if (binding.spinnerType.selectedItem.equals("Before")) {
                                if(clickedButtonCountBefore < 15){
                                    clickedButtonCountBefore++
                                    baseViewModel.setBeforeCount(clickedButtonCountBefore)
                                }
                                attachmentEntity = addAttachmentViewModel.requestForImageDB(
                                    convertedImage1,
                                    selectedImageType,
                                    binding.etDescription.text.toString(),
                                    taskId,
                                )
                                System.out.println("attachmentEntity " + convertedImage1 + " " + attachmentEntity)
                                db.bookDao().insertAddAttachmentSet(attachmentEntity)
                                db.bookDao().update("Before Task", taskId, binding.etDescription.text.toString())
                                db.bookDao().updateLOC("Before Task",taskId)
                                db.bookDao().updateBeforeCount(clickedButtonCountBefore,taskId)
                            } else if (binding.spinnerType.selectedItem.equals("After")) {
                                if(clickedButtonCountAfter < 15){
                                    clickedButtonCountAfter++
                                    baseViewModel.setAfterCount(clickedButtonCountAfter)
                                }
                                attachmentEntity = addAttachmentViewModel.requestForImageDB(
                                    convertedImage1,
                                    selectedImageType,
                                    binding.etDescription.text.toString(),
                                    taskId,
                                )
                                db.bookDao().insertAddAttachmentSet(attachmentEntity)
                                db.bookDao().update("After Task", taskId, binding.etDescription.text.toString())
                                db.bookDao().updateLOC("After Task",taskId)
                                db.bookDao().updateBeforeCount(clickedButtonCountAfter,taskId)
                            } else if (binding.spinnerType.selectedItem.equals("Material Picture")) {
                                if(clickedButtonCountMaterialPicture < 15){
                                    clickedButtonCountMaterialPicture++
                                }
                                attachmentEntity = addAttachmentViewModel.requestForImageDB(
                                    convertedImage1,
                                    selectedImageType,
                                    binding.etDescription.text.toString(),
                                    taskId,
                                )
                                db.bookDao().insertAddAttachmentSet(attachmentEntity)
                                db.bookDao().update("Material Picture", taskId, binding.etDescription.text.toString())
                                db.bookDao().updateLOC("Material Picture",taskId)
                            } else if (binding.spinnerType.selectedItem.equals("Inspection")) {
                                if(clickedButtonCountInspection < 15){
                                    clickedButtonCountInspection++
                                }
                                attachmentEntity = addAttachmentViewModel.requestForImageDB(
                                    convertedImage1,
                                    selectedImageType,
                                    binding.etDescription.text.toString(),
                                    taskId,
                                )
                                db.bookDao().insertAddAttachmentSet(attachmentEntity)
                                db.bookDao().update("Inspection", taskId, binding.etDescription.text.toString())
                                db.bookDao().updateLOC("Inspection",taskId)
                            }
                        }

                        val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                        intent.putExtra("taskid", taskId)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    pd.dismiss()
                    Toast.makeText(this@AddAttachmentActivity, "Add images to continue", Toast.LENGTH_SHORT).show()
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

    private fun timeStamp(bitmap:Bitmap){
        val x = 5f // X-coordinate where you want to draw the text
        val y = 20f
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val currentDate = sdf.format(Date())
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.setColor(Color.RED)
        paint.setTextSize(12f)
        canvas.drawText("Date: \n$currentDate", x, y, paint)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (fromEvent) {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    timeStamp(bitmap)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDBNoAccess = baos.toByteArray()
                    convertedImageNoAccess = Base64.encodeToString(convertedImageDBNoAccess, Base64.DEFAULT)
                    binding.captureImage.setImageBitmap(bitmap)
                } else {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    timeStamp(bitmap)
                    binding.captureImage.setImageBitmap(bitmap)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos) // bm is the bitmap object
                    convertedImageDB1 = baos.toByteArray()
                    convertedImage1 = Base64.encodeToString(convertedImageDB1, Base64.DEFAULT)
                }
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                if (fromEvent) {
                    val uri = data?.getData()
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        80,
                        baos1
                    ) // bm is the bitmap object
                    convertedImageDBNoAccess = baos1.toByteArray()
                    convertedImageNoAccess = Base64.encodeToString(convertedImageDBNoAccess, Base64.DEFAULT)
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
                            selectedImageNoAccess
                        )
                        val intent = Intent(this@AddAttachmentActivity, TaskActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        lifecycleScope.launch {
                            if (binding.spinnerType.selectedItem.equals("Before")) {
                                if (clickedButtonCountBefore < 15) {
                                    clickedButtonCountBefore++
                                    baseViewModel.setBeforeCount(clickedButtonCountBefore)
                                    System.out.println("suvarna before " + clickedButtonCountBefore)
                                }
                            } else if (binding.spinnerType.selectedItem.equals("After")) {
                                if (clickedButtonCountAfter < 15) {
                                    clickedButtonCountAfter++
                                    baseViewModel.setAfterCount(clickedButtonCountAfter)
                                }
                            }else if (binding.spinnerType.selectedItem.equals("Material Picture")) {
                                if(clickedButtonCountMaterialPicture < 15){
                                    clickedButtonCountMaterialPicture++
                                }
                            } else if (binding.spinnerType.selectedItem.equals("Inspection")) {
                                if(clickedButtonCountInspection < 15){
                                    clickedButtonCountInspection++
                                }
                            }
                            val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                            intent.putExtra("taskid", taskId)
                            startActivity(intent)
                            finish()
                        }
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