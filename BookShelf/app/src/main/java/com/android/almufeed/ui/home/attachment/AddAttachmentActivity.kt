package com.android.almufeed.ui.home.attachment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Rating
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.android.almufeed.R
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.business.domain.utils.isOnline
import com.android.almufeed.databinding.ActivityAddAttachmentBinding
import com.android.almufeed.datasource.cache.database.BookDatabase
import com.android.almufeed.datasource.cache.models.offlineDB.AttachmentEntity
import com.android.almufeed.datasource.cache.models.offlineDB.EventsEntity
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity
import com.android.almufeed.ui.home.TaskDetailsActivity
import com.android.almufeed.ui.home.events.AddEventsActivity
import com.android.almufeed.ui.home.events.AddEventsViewModel
import com.android.almufeed.ui.home.rateus.RatingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_attachment.capture_image1
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
    private var convertedImage1 : String = ""
    private var convertedImage2 : String = ""
    private var convertedImage3 : String = ""
    private var convertedImage4 : String = ""
    private var convertedImage5 : String = ""
    private var convertedImage6 : String = ""
    private var convertedImageDB1 : ByteArray = "".toByteArray()
    private var convertedImageDB2 : ByteArray = "".toByteArray()
    private var convertedImageDB3 : ByteArray = "".toByteArray()
    private var convertedImageDB4 : ByteArray = "".toByteArray()
    private var convertedImageDB5 : ByteArray = "".toByteArray()
    private var convertedImageDB6 : ByteArray = "".toByteArray()
    private var Image1 : Boolean = false
    private var Image2 : Boolean = false
    private var Image3 : Boolean = false
    private var Image4 : Boolean = false
    private var Image5 : Boolean = false
    private var Image6 : Boolean = false
    private var selectedImageType : Int = -1
    private lateinit var taskId : String
    private lateinit var attachmentEntity : AttachmentEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAttachmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        binding.toolbar.linTool.visibility = View.VISIBLE
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm")
        val currentDate = sdf.format(Date())
        binding.txtCurrentDateTime.setText(currentDate)

        binding.toolbar.incToolbarEvent.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@AddAttachmentActivity, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        binding.toolbar.incToolbarAttachment.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@AddAttachmentActivity, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
        })

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, imageType)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter

        binding.spinnerType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
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

            if(convertedImage1.isEmpty() && convertedImage2.isEmpty() && convertedImage3.isEmpty()){
                Toast.makeText(this@AddAttachmentActivity,"Please add images", Toast.LENGTH_SHORT).show()

            }else if(selectedImageType < 0){
            Toast.makeText(this@AddAttachmentActivity,"Please select image type", Toast.LENGTH_SHORT).show()

            }else if(convertedImage1.isEmpty() || convertedImage2.isEmpty() || convertedImage3.isEmpty()){
                Toast.makeText(this@AddAttachmentActivity,"Add minimum 3 images", Toast.LENGTH_SHORT).show()
            }else{
                pd = Dialog(this, android.R.style.Theme_Black)
                val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
                pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
                pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
                pd.setContentView(view)
                pd.show()
                if(isOnline(this@AddAttachmentActivity)){
                    if(binding.spinnerType.selectedItem.equals("Before")){
                        addEventsViewModel.saveForEvent(taskId,binding.etDescription.text.toString(),"Before Task")
                    }else if(binding.spinnerType.selectedItem.equals("After")){
                        addEventsViewModel.saveForEvent(taskId,binding.etDescription.text.toString(),"After Task")
                    }
                    addAttachmentViewModel.requestForImage(convertedImage1,convertedImage2,convertedImage3,convertedImage4,convertedImage5,convertedImage6,selectedImageType,binding.etDescription.text.toString(),taskId)
                }else{
                    pd.dismiss()
                    val db = Room.databaseBuilder(this@AddAttachmentActivity, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()
                    lifecycleScope.launch {
                        attachmentEntity = addAttachmentViewModel.requestForImageDB(convertedImageDB1,convertedImageDB2,convertedImageDB3,convertedImageDB4,
                            convertedImageDB5,convertedImageDB6,selectedImageType,binding.etDescription.text.toString(),taskId)
                    }
                    if(binding.spinnerType.selectedItem.equals("Before")){
                        db.bookDao().insertAddAttachmentSet(attachmentEntity)
                        db.bookDao().update("Before Task",taskId,binding.etDescription.text.toString())
                    }else if(binding.spinnerType.selectedItem.equals("After")){
                        db.bookDao().insertAddAttachmentSet(attachmentEntity)
                        db.bookDao().update("After Task",taskId,binding.etDescription.text.toString())
                    }
                    val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
                }
              /*  if(binding.spinnerType.selectedItem.equals("Before")){
                    addEventsViewModel.saveForEvent(taskId,"comments","Before Task")
                }else if(binding.spinnerType.selectedItem.equals("After")){
                    addEventsViewModel.saveForEvent(taskId,"comments","After Task")
                }
                addAttachmentViewModel.requestForImage(convertedImage1,convertedImage2,convertedImage3,convertedImage4,
                    convertedImage5,convertedImage6,selectedImageType,binding.etDescription.text.toString(),taskId)*/
            }
            /*val intent = Intent(this@AddAttachmentActivity, RatingActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)*/
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
                if(Image1){
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.captureImage1.setImageBitmap(bitmap)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    convertedImageDB1 = baos.toByteArray()
                    convertedImage1 = Base64.encodeToString(convertedImageDB1, Base64.DEFAULT)
                }else if(Image2){
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.captureImage2.setImageBitmap(bitmap)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    convertedImageDB2 = baos.toByteArray()
                    convertedImage2 = Base64.encodeToString(convertedImageDB2, Base64.DEFAULT)
                }else if(Image3){
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.captureImage3.setImageBitmap(bitmap)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    convertedImageDB3 = baos.toByteArray()
                    convertedImage3 = Base64.encodeToString(convertedImageDB3, Base64.DEFAULT)
                }else if(Image4){
                    binding.linImage2.visibility = View.VISIBLE
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.captureImage4.setImageBitmap(bitmap)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    convertedImageDB4 = baos.toByteArray()
                    convertedImage4 = Base64.encodeToString(convertedImageDB4, Base64.DEFAULT)
                }else if(Image5){
                    binding.linImage2.visibility = View.VISIBLE
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.captureImage5.setImageBitmap(bitmap)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    convertedImageDB5 = baos.toByteArray()
                    convertedImage5 = Base64.encodeToString(convertedImageDB5, Base64.DEFAULT)
                }else if(Image6){
                    binding.linImage2.visibility = View.VISIBLE
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.captureImage6.setImageBitmap(bitmap)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    convertedImageDB6 = baos.toByteArray()
                    convertedImage6 = Base64.encodeToString(convertedImageDB6, Base64.DEFAULT)
                }
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                if(Image1){
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
                    binding.captureImage1.setImageURI(uri)

                    val bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1) // bm is the bitmap object
                    convertedImageDB1 = baos1.toByteArray()
                    convertedImage1 = Base64.encodeToString(convertedImageDB1, Base64.DEFAULT)
                }else if(Image2){
                    val uri = data?.getData()
                    binding.captureImage2.setImageURI(uri)

                    val bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1) // bm is the bitmap object
                    convertedImageDB2 = baos1.toByteArray()
                    convertedImage2 = Base64.encodeToString(convertedImageDB2, Base64.DEFAULT)
                }else if(Image3){
                    val uri = data?.getData()
                    binding.captureImage3.setImageURI(uri)

                    val bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1) // bm is the bitmap object
                    convertedImageDB3 = baos1.toByteArray()
                    convertedImage3 = Base64.encodeToString(convertedImageDB3, Base64.DEFAULT)
                }else if(Image4){
                    val uri = data?.getData()
                    binding.captureImage4.setImageURI(uri)

                    val bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1) // bm is the bitmap object
                    convertedImageDB4 = baos1.toByteArray()
                    convertedImage4 = Base64.encodeToString(convertedImageDB4, Base64.DEFAULT)
                }else if(Image5){
                    val uri = data?.getData()
                    binding.captureImage5.setImageURI(uri)

                    val bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1) // bm is the bitmap object
                    convertedImageDB5 = baos1.toByteArray()
                    convertedImage5 = Base64.encodeToString(convertedImageDB5, Base64.DEFAULT)
                }else if(Image6){
                    val uri = data?.getData()
                    binding.captureImage6.setImageURI(uri)

                    val bitmap1 = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    val baos1 = ByteArrayOutputStream()
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1) // bm is the bitmap object
                    convertedImageDB6 = baos1.toByteArray()
                    convertedImage6 = Base64.encodeToString(convertedImageDB6, Base64.DEFAULT)
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
                    val intent = Intent(this@AddAttachmentActivity, TaskDetailsActivity::class.java)
                    intent.putExtra("taskid", taskId)
                    startActivity(intent)
                    finish()
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