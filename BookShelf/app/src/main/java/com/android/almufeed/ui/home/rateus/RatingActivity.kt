package com.android.almufeed.ui.home.rateus

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.android.almufeed.R
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.databinding.ActivityRatingBinding
import com.android.almufeed.ui.home.attachment.AttachmentList
import com.android.almufeed.ui.home.events.AddEventsActivity
import com.android.almufeed.ui.home.events.AddEventsViewModel
import com.android.almufeed.ui.launchpad.DashboardActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kyanogen.signatureview.SignatureView
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class RatingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRatingBinding
    private val ratingViewModel: RatingViewModel by viewModels()
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private lateinit var pd : Dialog
    private var customerSignature : String = ""
    private var techSignature : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val taskId = intent.getStringExtra("taskid").toString()
        setSupportActionBar(binding.toolbar.incToolbarWithCenterLogoToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Task : $taskId"
        }

        binding.toolbar.linTool.visibility = View.VISIBLE
        binding.toolbar.incToolbarEvent.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@RatingActivity, AddEventsActivity::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
        })

        binding.toolbar.incToolbarAttachment.setOnClickListener (View.OnClickListener { view ->
            val intent = Intent(this@RatingActivity, AttachmentList::class.java)
            intent.putExtra("taskid", taskId)
            startActivity(intent)
            finish()
        })

        binding.btnCustomer.setOnClickListener (View.OnClickListener { view ->
            showMessageDialog()
        })

        binding.btnTechnic.setOnClickListener (View.OnClickListener { view ->
            showSignatureDialog("technician")
        })

        binding.btnComplete.setOnClickListener (View.OnClickListener { view ->

            if(binding.rating.rating > 0 && binding.emailInput.text.toString().isNotEmpty() && binding.imgSignatureCustomer.drawable != null && binding.imgSignatureTech.drawable != null){
                pd = Dialog(this, android.R.style.Theme_Black)
                val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
                pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
                pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
                pd.setContentView(view)
                pd.show()
                addEventsViewModel.saveForEvent(taskId,"comments","Completed")
                val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm")
                val currentDate = sdf.format(Date())
                ratingViewModel.requestForRating(customerSignature,techSignature,binding.rating.rating.toDouble(),binding.emailInput.text.toString(),currentDate,taskId)
            }else{
                Toast.makeText(this@RatingActivity,"All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        })

        subscribeObservers()
    }

    private fun showSignatureDialog(tag:String) {
        val mBuilder = MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
        mBuilder.setView(layoutInflater.inflate(R.layout.dialog_signature, null))
        val mDialog = mBuilder.create()
        mDialog.setCancelable(false)
        mDialog.setCanceledOnTouchOutside(true)
        mDialog.show()

        val btnClear = mDialog.findViewById<Button>(R.id.btnClear)
        val btnSave = mDialog.findViewById<Button>(R.id.btnSave)
        val btnCancel = mDialog.findViewById<ImageView>(R.id.btnCancel)
        val signatureView = mDialog.findViewById<SignatureView>(R.id.signature_view)

        btnClear?.setOnClickListener {
            if (signatureView != null) {
                signatureView.clearCanvas()
            }
        }

        btnCancel?.setOnClickListener {
            mDialog.dismiss()
        }

        btnSave?.setOnClickListener{
            val bitmap: Bitmap = signatureView!!.getSignatureBitmap()
            if (bitmap != null) {
                if(tag == "customer"){
                    binding.imgSignatureCustomer.visibility = View.VISIBLE
                    binding.imgSignatureCustomer.setImageBitmap(bitmap)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    val byteArrayImage: ByteArray = baos.toByteArray()
                    customerSignature = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                    mDialog.dismiss()
                }else if(tag == "technician"){
                    binding.imgSignatureTech.visibility = View.VISIBLE
                    binding.imgSignatureTech.setImageBitmap(bitmap)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
                    val byteArrayImage: ByteArray = baos.toByteArray()
                    techSignature = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                    mDialog.dismiss()
                }
            }
        }
    }

  /*  private fun showMessageDialog() {
        val builder = AlertDialog.Builder(this@RatingActivity)
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        //builder.setTitle(R.string.dialogTitle)
        //set message for alert dialog
        builder.setMessage(R.string.dialog_message)
        //builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("OK"){dialogInterface, which ->
            alertDialog.dismiss()
            showSignatureDialog("customer")
        }
    }*/

    @SuppressLint("SetTextI18n")
    private fun showMessageDialog() {
        val mBuilder = MaterialAlertDialogBuilder(this@RatingActivity, R.style.MaterialAlertDialog_Rounded)
        mBuilder.setView(layoutInflater.inflate(R.layout.alert_dialog, null))
        val mDialog = mBuilder.create()
        mDialog.setCancelable(false)
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.show()

        val tvmsg = mDialog.findViewById<TextView>(R.id.tv_msg)
        val btnOk = mDialog.findViewById<Button>(R.id.btn_ok)

        btnOk?.setOnClickListener {
            mDialog.dismiss()
            showSignatureDialog("customer")
        }
    }

    private fun subscribeObservers() {
        ratingViewModel.myRateDataSTate.observe(this@RatingActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@RatingActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    if(dataState.data.Success){
                        Toast.makeText(this@RatingActivity,"Task Completed", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RatingActivity, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {

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