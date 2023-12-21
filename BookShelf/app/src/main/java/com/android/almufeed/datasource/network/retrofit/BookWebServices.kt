package com.android.almufeed.datasource.network.retrofit

import com.android.almufeed.datasource.network.models.attachment.AttachmentRequestModel
import com.android.almufeed.datasource.network.models.attachment.AttachmentResponseModel
import com.android.almufeed.datasource.network.models.attachment.GetAttachmentRequestModel
import com.android.almufeed.datasource.network.models.attachment.GetAttachmentResponseModel
import com.android.almufeed.datasource.network.models.events.GetEventListResponseModel
import com.android.almufeed.datasource.network.models.events.SaveEventRequestModel
import com.android.almufeed.datasource.network.models.events.SaveEventResponseModel
import com.android.almufeed.datasource.network.models.instructionSet.InstructionSetRequestModel
import com.android.almufeed.datasource.network.models.instructionSet.InstructionSetResponseModel
import com.android.almufeed.datasource.network.models.login.LoginRequest
import com.android.almufeed.datasource.network.models.login.LoginResponse
import com.android.almufeed.datasource.network.models.rating.RatingRequestModel
import com.android.almufeed.datasource.network.models.rating.RatingResponseModel
import com.android.almufeed.datasource.network.models.tasklist.TaskListRequest
import com.android.almufeed.datasource.network.models.tasklist.TaskListResponse
import com.android.almufeed.datasource.network.models.token.CreateTokenResponse
import com.android.almufeed.datasource.network.models.updateInstruction.UpdateInstructionSetRequestModel
import com.android.almufeed.datasource.network.models.updateInstruction.UpdateInstructionSetResponseModel
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