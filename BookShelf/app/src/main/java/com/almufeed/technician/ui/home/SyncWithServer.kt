package com.almufeed.technician.ui.home

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.room.Room
import com.almufeed.technician.R
import com.almufeed.technician.business.domain.state.DataState
import com.almufeed.technician.business.domain.utils.exhaustive
import com.almufeed.technician.datasource.cache.database.BookDatabase
import com.almufeed.technician.datasource.network.models.token.CreateTokenResponse
import com.almufeed.technician.ui.base.BaseViewModel
import com.almufeed.technician.ui.home.attachment.AddAttachmentViewModel
import com.almufeed.technician.ui.home.customer.ProofOfAttendenceViewModel
import com.almufeed.technician.ui.home.events.AddEventsViewModel
import com.almufeed.technician.ui.home.instructionSet.CheckListViewModel
import com.almufeed.technician.ui.home.rateus.RatingViewModel
import com.almufeed.technician.ui.launchpad.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class SyncWithServer : AppCompatActivity() {
    private val proofOfAttendenceViewModel: ProofOfAttendenceViewModel by viewModels()
    private val checkListViewModel: CheckListViewModel by viewModels()
    private val addEventsViewModel: AddEventsViewModel by viewModels()
    private val addAttachmentViewModel: AddAttachmentViewModel by viewModels()
    private val ratingViewModel: RatingViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var pd : Dialog
    private lateinit var pd1 : Dialog
    private lateinit var pd2 : Dialog
    private lateinit var pd3 : Dialog
    private lateinit var pd4 : Dialog
    private lateinit var pd5 : Dialog
    private lateinit var pd6 : Dialog
    private lateinit var db :BookDatabase
    private var customerFlag :Boolean = false
    private var pictureFlag :Boolean = false
    private var instructionSetFlag :Boolean = false
    private var RatingFlag :Boolean = false
    private var eventFlag :Boolean = false

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

        getTokenApi()
    }

    private fun getTokenApi() {
        val mediaType = APIServices.mediaType.toMediaType()
        val body = APIServices.body.toRequestBody(mediaType)
        val getToken = APIServices.create().getProducts(body)
        Log.d("products getToken", getToken.request().body.toString())
        try{
            getToken.enqueue(object : Callback<CreateTokenResponse> {
                override fun onResponse(
                    call: Call<CreateTokenResponse>,
                    response: Response<CreateTokenResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("products", response.body().toString())
                        if (response.body() != null) {
                            var accessToken = "Bearer " + response.body()!!.access_token
                            baseViewModel.setToken(response.body()!!.access_token)
                            val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                            val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                            myEdit.putString("token", accessToken)
                            myEdit.commit()

                            Handler(Looper.getMainLooper()).postDelayed({
                                sendCustomerDetails()
                                sendAttachment()
                                sendInstructionSet()
                                sendRating()
                                sendEventDetails()
                            }, 1000)
                        }
                    }
                }

                override fun onFailure(call: Call<CreateTokenResponse>, t: Throwable) {
                    Log.d("failure", t.message.toString())
                    Toast.makeText(this@SyncWithServer, "onFailure: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }catch(e:Exception){
            e.printStackTrace()
            pd.dismiss()
            Toast.makeText(this@SyncWithServer, "Some error. Please try later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subscribeObserversForInstructionSet(taskId: String) {
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
                    pd.dismiss()
                    db.bookDao().deleteStoreInstructionByColumnValue(taskId)
                }

                else -> {}
            }.exhaustive
        }
    }

    private fun subscribeObserversForAttachment(taskId: String) {
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
                    pd.dismiss()
                    db.bookDao().deleteAttachmentByColumnValue(taskId)
                }

                else -> {}
            }.exhaustive
        }
    }

    private fun subscribeObserversForRating(taskId: String) {
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
                    db.bookDao().deleteRatingByColumnValue(taskId)
                }

                else -> {}
            }.exhaustive
        }
    }

    private fun subscribeObserversForCustomerDetail(taskId:String) {
        proofOfAttendenceViewModel.myRateDataSTate.observe(this@SyncWithServer) { dataState ->
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
                    db.bookDao().deleteCustomerDetailsByColumnValue(taskId)
                }

                else -> {}
            }.exhaustive
        }
    }
    private fun subscribeObserversForEvent(taskId: String) {

        addEventsViewModel.mySetEventDataSTate.observe(this@SyncWithServer) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@SyncWithServer,dataState.exception.message, Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    pd.dismiss()
                    db.bookDao().deleteEventsByColumnValue(taskId)
                    Intent(this@SyncWithServer, DashboardActivity::class.java).apply {
                        startActivity(this)
                    }
                    Toast.makeText(this@SyncWithServer,"Sync Completed", Toast.LENGTH_SHORT).show()
                }
            }.exhaustive
        }
    }

    private fun sendRating() {
        pd.show()
        val ratingList = db.bookDao().getAllRating()
        if(ratingList.size > 0){
            for (i in ratingList.indices) {
                ratingViewModel.requestForRating(
                    ratingList[i].customerSignature,
                    ratingList[i].techiSignature,
                    ratingList[i].rating,
                    ratingList[i].comment,
                    ratingList[i].dateTime,
                    ratingList[i].task_id
                )
                subscribeObserversForRating(ratingList[i].task_id)
            }
        }else{

        }
    }
    private fun sendEventDetails() {
        pd.show()
        val eventList = db.bookDao().getAllEvents()
        if(eventList.size > 0){
            for (i in eventList.indices) {
                addEventsViewModel.saveForEvent(
                    eventList[i].taskId,
                    eventList[i].Comments,
                    eventList[i].Events
                )
                subscribeObserversForEvent(eventList[i].taskId)
            }
        }else{
            Intent(this@SyncWithServer, DashboardActivity::class.java).apply {
                startActivity(this)
            }
            Toast.makeText(this@SyncWithServer,"Sync Completed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendAttachment() {
        pd.show()
        // send attachments to server
        val attachmentList = db.bookDao().getAllAttachmentSet()
        System.out.println("attachmentList " + attachmentList.size)

        if(attachmentList.size > 0){
            for (i in attachmentList.indices) {
                addAttachmentViewModel.requestForImage(attachmentList[i].Image,attachmentList[i].type,attachmentList[i].description,attachmentList[i].taskId)
                subscribeObserversForAttachment(attachmentList[i].taskId)
            }
        }
    }

    private fun sendInstructionSet() {
        pd.show()
        // send instruction set to server
        val instructionList = db.bookDao().getAllInstructionSet()
        if(instructionList.size > 0){
            for (i in instructionList.indices) {
                checkListViewModel.updateForStep(instructionList[i].Refrecid,instructionList[i].FeedbackType,instructionList[i].AnswerSet,instructionList[i].taskId)
                subscribeObserversForInstructionSet(instructionList[i].taskId)
            }
        }
    }

    private fun sendCustomerDetails() {
        // send instruction set to server
        pd.show()
        val customerDetail = db.bookDao().getAllCustomerDetails()
        if(customerDetail.size > 0){
            for (i in customerDetail.indices) {
                proofOfAttendenceViewModel.requestForCustomer(customerDetail[i].customerName,customerDetail[i].Email,customerDetail[i].Phone,customerDetail[i].taskId)
                subscribeObserversForCustomerDetail(customerDetail[i].taskId)
            }
        }
    }
}