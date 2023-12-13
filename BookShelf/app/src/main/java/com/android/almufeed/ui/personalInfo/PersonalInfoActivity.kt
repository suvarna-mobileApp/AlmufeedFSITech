package com.android.almufeed.ui.personalInfo

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.almufeed.R
import com.android.almufeed.business.domain.utils.collectLatestFlow
import com.android.almufeed.business.domain.utils.errorListener
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.databinding.ActivityPersonalInfoBinding
import com.android.almufeed.ui.camera.CameraActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

@AndroidEntryPoint
class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var profilePicMultiPart: MultipartBody.Part
    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()
    private lateinit var binding: ActivityPersonalInfoBinding
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var imageBitmap: Bitmap? = null
    private var compressedImageFile: File? = null
    private var isCameraSelected: Boolean = false
    private var userProfilePicUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        binding.apply {

            edFirstName.errorListener(nameInput)
            edLastName.errorListener(nameInputLast)
            edEmail.errorListener(emailInput)
            progressBar.visibility = View.INVISIBLE

            imageButtonPickImage.setOnClickListener {
                val mBuilder = MaterialAlertDialogBuilder(
                    this@PersonalInfoActivity,
                    R.style.MaterialAlertDialog_Rounded
                )
                mBuilder.setView(
                    LayoutInflater.from(this@PersonalInfoActivity)
                        .inflate(R.layout.dialog_image_picker, null)
                )

                val mDialog = mBuilder.create()
                mDialog.setCanceledOnTouchOutside(true)
                mDialog.show()

                val btnCamera = mDialog.findViewById<MaterialButton>(R.id.btn_camera)
                val btnGallery = mDialog.findViewById<MaterialButton>(R.id.btn_gallery)

                btnCamera?.setOnClickListener {
                    mDialog.dismiss()
                    if (checkCameraPermissions()) {
                        isCameraSelected = true
                        launchCamera()
                    } else {
                        requestCameraPermission()
                    }
                }

                btnGallery?.setOnClickListener {
                    mDialog.dismiss()
                    if (checkGalleryPermissions()) {
                        isCameraSelected = false
                        openGallery()
                    } else {
                        requestGalleryPermission()
                    }
                }
            }

            btnNext.setOnClickListener {

                if (validateScreen()) {
                    val firstname = binding.nameInput.text.toString().trim()
                    val email = binding.emailInput.text.toString().trim()
                    val lastname = binding.nameInputLast.text.toString().trim()
                    personalInfoViewModel.navigateToLaunchpad(firstname,lastname,email,userProfilePicUrl)
                }
            }
        }
        cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isCameraPermissionGranted =
                    permissions[Manifest.permission.CAMERA] ?: isCameraPermissionGranted
                if (isCameraPermissionGranted) {
                    launchCamera()
                }
            }

        galleryPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isWriteExternalPermissionGranted =
                    permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                        ?: isWriteExternalPermissionGranted
                if (isWriteExternalPermissionGranted) {
                    openGallery()
                }
            }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        collectLatestFlow(personalInfoViewModel.taskEvent) { event ->

            when (event) {
                is PersonalInfoViewModel.TaskEvent.NavigateToDashboard -> {
                    gotoLaunchpadPage()
                }
                else -> {}
            }.exhaustive
        }
    }

    private fun gotoLaunchpadPage() {
        /*Intent(this@PersonalInfoActivity, LaunchpadActivity::class.java).apply {
            startActivity(this)
        }*/
    }

    private fun validateScreen(): Boolean {

        var isValid = true

        /*if(imageBitmap == null){
            Toast.makeText(
                this@PersonalInfoActivity,
                "Please upload a profile picture",
                Toast.LENGTH_SHORT
            ).show()
            binding.profileImage.requestFocus()
        }*/

        if (binding.nameInput.text.toString().trim().isNullOrEmpty()) {
            binding.edFirstName.error = "This field is required"
            if (isValid) binding.nameInput.requestFocus()
            isValid = false
        }
        if (binding.nameInputLast.text.toString().trim().isNullOrEmpty()) {
            //binding.nameInputLast.error = "This field is required"
            binding.edLastName.error = "This field is required"
            if (isValid) binding.nameInputLast.requestFocus()
            isValid = false
        }
        if (binding.emailInput.text.toString().trim().isNullOrEmpty()) {
            //binding.nameInputLast.error = "This field is required"
            binding.edEmail.error = "This field is required"
            if (isValid) binding.emailInput.requestFocus()
            isValid = false
        }
        if (binding.emailInput.text.toString().trim().isNotEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.text.toString().trim())
                    .matches()
            ) {
                //binding.emailInput.error = "Enter valid Email address !"
                binding.edEmail.error = "Enter valid Email address !"
                if (isValid) binding.nameInputLast.requestFocus()
                isValid = false
            }
        }

        return isValid
    }
//    private var galleryResultLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // There are no request codes
//                val data: Intent? = result.data
//                val imageUri = data?.data
//                binding.profileImage.setImageURI(imageUri)
//                imageBitmap = imageUriToBitmap(imageUri)
//            }
//        }

    private fun imageUriToBitmap(imageUri: Uri?): Bitmap? {
        val bitmap: Bitmap? = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        } else {
            val source =
                imageUri?.let { ImageDecoder.createSource(contentResolver, it) }

            source?.let { ImageDecoder.decodeBitmap(it) }
        }
        return bitmap
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, stream)
        val image = stream.toByteArray()
        return Base64.encodeToString(image, Base64.DEFAULT)
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
//            cameraResult.launch(takePictureIntent)
            Intent(applicationContext, CameraActivity::class.java)
                .apply {
                    cameraLauncher.launch(this)
                }
        } catch (e: ActivityNotFoundException) {

        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK)
            .setType("image/*")
        galleryResult.launch(gallery)
    }

    private var galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val imageType = imageUri?.let { getMimeType(it) }
                if (imageType == "gif") {
                    Toast.makeText(
                        this,
                        getString(R.string.theSelectedImageIs),
                        Toast.LENGTH_SHORT
                    ).show()
                } else
                    imageUri?.let { addSelectedImage(it) }
            }
        }

    private fun getMimeType(uri: Uri): String? {

        //Check uri format to avoid null
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(contentResolver?.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    private fun addSelectedImage(imageUri: Uri) {
        /*val file = File(imageUri.path)
        lifecycleScope.launch {
            compressedImageFile = Compressor.compress(this@PersonalInfoActivity, file)
        }*/
        //val uri = Uri.fromFile(compressedImageFile)
        //compressImage(imageUri.path.toString(),this)
        userProfilePicUrl = imageUri.path.toString()
        imageBitmap = imageUriToBitmap(imageUri)
        val bitmap = Bitmap.createScaledBitmap(imageBitmap!!, 300, 300, true)
        bitmap?.apply {
            binding.profileImage.setImageBitmap(this)
        }
        profilePicMultiPart = uriToMultiPart(imageUri)
    }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmapUri = result.data?.extras?.get(CameraActivity.IMAGE_URI)
                addSelectedImage(bitmapUri as Uri)
            }
        }

    private fun uriToMultiPart(imageUri: Uri): MultipartBody.Part {
        return if (isCameraSelected) {
            val file = File(imageUri.path)
            val requestFile = RequestBody.create("multipart/from-data".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        } else {
            val openInputStream = contentResolver.openInputStream(imageUri)
            val requestFile = RequestBody.create(
                "multipart/from-data".toMediaTypeOrNull(),
                openInputStream!!.readBytes()
            )
            MultipartBody.Part.createFormData("file", "testdoc", requestFile)
        }
    }

    private fun requestCameraPermission() {
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if (!isCameraPermissionGranted) {
            permissionRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionRequest.isNotEmpty()) {
            cameraPermissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private fun requestGalleryPermission() {

        isWriteExternalPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if (!isWriteExternalPermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionRequest.isNotEmpty()) {
            this.galleryPermissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private fun checkCameraPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }

    private fun checkGalleryPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }


    companion object {
        private var isCameraPermissionGranted = false
        private var isWriteExternalPermissionGranted = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
