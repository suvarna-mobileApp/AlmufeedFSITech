package com.android.almufeed.ui.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.room.Room
import com.android.almufeed.R
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.datasource.cache.database.BookDatabase
import com.android.almufeed.ui.home.attachment.AddAttachmentViewModel
import com.android.almufeed.ui.home.events.AddEventsViewModel
import com.android.almufeed.ui.home.instructionSet.CheckListViewModel
import com.android.almufeed.ui.home.rateus.RatingViewModel
import com.android.almufeed.ui.launchpad.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class SyncWithServer : AppCompatActivity() {
    private val checkListViewModel: CheckListViewModel by viewModels()
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private val addAttachmentViewModel: AddAttachmentViewModel by viewModels()
    private val ratingViewModel: RatingViewModel by viewModels()
    private lateinit var pd : Dialog
    private lateinit var db :BookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_with_server)
        db = Room.databaseBuilder(this, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()
        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)
        pd.show()

        sendInstructionSet()
    }

    private fun subscribeObserversForInstructionSet() {
        checkListViewModel.myUpdateDataSTate.observe(this@SyncWithServer) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@SyncWithServer,"Some error, please try later", Toast.LENGTH_SHORT).show()
                    Intent(this@SyncWithServer, DashboardActivity::class.java).apply {
                        startActivity(this)
                    }
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    sendAttachment()
                }

                else -> {}
            }.exhaustive
        }
    }

    private fun subscribeObserversForAttachment() {
        addAttachmentViewModel.myImageDataSTate.observe(this@SyncWithServer) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@SyncWithServer,"Some error, please try later", Toast.LENGTH_SHORT).show()
                    Intent(this@SyncWithServer, DashboardActivity::class.java).apply {
                        startActivity(this)
                    }
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    sendRating()
                }

                else -> {}
            }.exhaustive
        }
    }

    private fun subscribeObserversForRating() {
        ratingViewModel.myRateDataSTate.observe(this@SyncWithServer) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@SyncWithServer,"Some error, please try later", Toast.LENGTH_SHORT).show()
                    Intent(this@SyncWithServer, DashboardActivity::class.java).apply {
                        startActivity(this)
                    }
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    pd.dismiss()
                    Toast.makeText(this@SyncWithServer,"Sync Completed", Toast.LENGTH_SHORT).show()
                    Intent(this@SyncWithServer, DashboardActivity::class.java).apply {
                        startActivity(this)
                    }
                }

                else -> {}
            }.exhaustive
        }
    }

    private fun sendRating() {
        val ratingList = db.bookDao().getAllRating()
        val baos = ByteArrayOutputStream()

        for (i in ratingList.indices) {
            ratingList[i].customerSignature = baos.toByteArray()
            ratingList[i].techiSignature = baos.toByteArray()

            val convertedImage1 = Base64.encodeToString(ratingList[i].customerSignature, Base64.DEFAULT)
            val convertedImage2 = Base64.encodeToString(ratingList[i].techiSignature, Base64.DEFAULT)

            ratingViewModel.requestForRating(
                convertedImage1,
                convertedImage2,
                ratingList[i].rating,
                ratingList[i].comment,
                ratingList[i].dateTime,
                ratingList[i].task_id,
                ratingList[i].customerName,
                ratingList[i].Email,
                ratingList[i].Phone,
            )
        }
        subscribeObserversForRating()
    }

    private fun sendAttachment() {
        // send attachments to server
        val attachmentList = db.bookDao().getAllAttachmentSet()
        // convert to base64
        val baos = ByteArrayOutputStream()
        for (i in attachmentList.indices) {
            attachmentList[i].Image1 = baos.toByteArray()
            attachmentList[i].Image2 = baos.toByteArray()
            attachmentList[i].Image3 = baos.toByteArray()
            attachmentList[i].Image4 = baos.toByteArray()
            attachmentList[i].Image5 = baos.toByteArray()
            attachmentList[i].Image6 = baos.toByteArray()

            val convertedImage1 = Base64.encodeToString(attachmentList[i].Image1, Base64.DEFAULT)
            val convertedImage2 = Base64.encodeToString(attachmentList[i].Image2, Base64.DEFAULT)
            val convertedImage3 = Base64.encodeToString(attachmentList[i].Image3, Base64.DEFAULT)
            val convertedImage4 = Base64.encodeToString(attachmentList[i].Image4, Base64.DEFAULT)
            val convertedImage5 = Base64.encodeToString(attachmentList[i].Image5, Base64.DEFAULT)
            val convertedImage6 = Base64.encodeToString(attachmentList[i].Image6, Base64.DEFAULT)

            //addAttachmentViewModel.requestForImage(convertedImage1,convertedImage2,convertedImage3, convertedImage4,convertedImage5,convertedImage6,attachmentList[i].type,attachmentList[i].description,attachmentList[i].taskId)
        }
        subscribeObserversForAttachment()
    }

    private fun sendInstructionSet() {
        // send instruction set to server
        val instructionList = db.bookDao().getAllInstructionSet()
        for (i in instructionList.indices) {
            checkListViewModel.updateForStep(instructionList[i].Refrecid,instructionList[i].FeedbackType,instructionList[i].AnswerSet,instructionList[i].taskId)
        }
        subscribeObserversForInstructionSet()
    }
}