package com.almufeed.technician.datasource.network.retrofit

import com.almufeed.technician.datasource.network.models.attachment.AttachmentRequestModel
import com.almufeed.technician.datasource.network.models.attachment.AttachmentResponseModel
import com.almufeed.technician.datasource.network.models.attachment.GetAttachmentRequestModel
import com.almufeed.technician.datasource.network.models.attachment.GetAttachmentResponseModel
import com.almufeed.technician.datasource.network.models.customer.CustomerRequestModel
import com.almufeed.technician.datasource.network.models.customer.CustomerResponseModel
import com.almufeed.technician.datasource.network.models.events.GetEventListResponseModel
import com.almufeed.technician.datasource.network.models.events.SaveEventRequestModel
import com.almufeed.technician.datasource.network.models.events.SaveEventResponseModel
import com.almufeed.technician.datasource.network.models.instructionSet.InstructionSetRequestModel
import com.almufeed.technician.datasource.network.models.instructionSet.InstructionSetResponseModel
import com.almufeed.technician.datasource.network.models.login.LoginRequest
import com.almufeed.technician.datasource.network.models.login.LoginResponse
import com.almufeed.technician.datasource.network.models.rating.RatingRequestModel
import com.almufeed.technician.datasource.network.models.rating.RatingResponseModel
import com.almufeed.technician.datasource.network.models.tasklist.TaskListRequest
import com.almufeed.technician.datasource.network.models.tasklist.TaskListResponse
import com.almufeed.technician.datasource.network.models.token.CreateTokenResponse
import com.almufeed.technician.datasource.network.models.updateInstruction.UpdateInstructionSetRequestModel
import com.almufeed.technician.datasource.network.models.updateInstruction.UpdateInstructionSetResponseModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface BookWebServices {
    @POST("FSIAuthentication")
    suspend fun login(
        @Header("Authorization") authToken: String,
        @Body req: LoginRequest
    ): Response<LoginResponse>

    @POST("getTaskList")
    suspend fun getTaskList(
        @Header("Authorization") authToken: String,
        @Body req: TaskListRequest
    ): Response<TaskListResponse>

    @POST("getProblemList")
    suspend fun getProblemList(
        @Header("Authorization") authToken: String,
        @Body req: InstructionSetRequestModel
    ): Response<InstructionSetResponseModel>

    @POST("setProblemAnswer")
    suspend fun updateProblemList(
        @Header("Authorization") authToken: String,
        @Body req: UpdateInstructionSetRequestModel
    ): Response<UpdateInstructionSetResponseModel>

    @POST("setProblemImage")
    suspend fun addAttachment(
        @Header("Authorization") authToken: String,
        @Body req: AttachmentRequestModel
    ): Response<AttachmentResponseModel>

    @POST("getProblemImage")
    suspend fun getAttachment(
        @Header("Authorization") authToken: String,
        @Body req: GetAttachmentRequestModel
    ): Response<GetAttachmentResponseModel>

    @POST("getEventList")
    suspend fun getEventList(
        @Header("Authorization") authToken: String,
    ): Response<GetEventListResponseModel>

    @POST("setProblemRating")
    suspend fun setProblemRating(
        @Header("Authorization") authToken: String,
        @Body req: RatingRequestModel
    ): Response<RatingResponseModel>

    @POST("setCustomerDetail")
    suspend fun setCustomerDetail(
        @Header("Authorization") authToken: String,
        @Body req: CustomerRequestModel
    ): Response<CustomerResponseModel>

    @POST("setEventtoTask")
    suspend fun setEventTask(
        @Header("Authorization") authToken: String,
        @Body req: SaveEventRequestModel
    ): Response<SaveEventResponseModel>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("token")
    fun getProducts(@Body session: RequestBody): Call<CreateTokenResponse>

    @Headers("Content-Type: application/json")
    @POST("getTaskList")
    fun getTaskListForNotification(
        @Header("Authorization") authToken: String, @Body req: TaskListRequest): Call<TaskListResponse>
}