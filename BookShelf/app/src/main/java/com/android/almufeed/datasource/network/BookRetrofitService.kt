package com.android.almufeed.datasource.network

import com.android.almufeed.business.domain.state.DataState
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
import com.android.almufeed.datasource.network.models.updateInstruction.UpdateInstructionSetRequestModel
import com.android.almufeed.datasource.network.models.updateInstruction.UpdateInstructionSetResponseModel
import kotlinx.coroutines.flow.Flow

interface BookRetrofitService {
    suspend fun login(token: String, request: LoginRequest): Flow<DataState<LoginResponse>>
    suspend fun getTaskList(token: String, request: TaskListRequest): Flow<DataState<TaskListResponse>>
    suspend fun getProblemList(token: String, request: InstructionSetRequestModel): Flow<DataState<InstructionSetResponseModel>>
    suspend fun updateProblemList(token: String, request: UpdateInstructionSetRequestModel): Flow<DataState<UpdateInstructionSetResponseModel>>
    suspend fun addAttachment(token: String, request: AttachmentRequestModel): Flow<DataState<AttachmentResponseModel>>
    suspend fun getAttachment(token: String, request: GetAttachmentRequestModel): Flow<DataState<GetAttachmentResponseModel>>
    suspend fun getEventList(token: String): Flow<DataState<GetEventListResponseModel>>
    suspend fun setRating(token: String,request: RatingRequestModel): Flow<DataState<RatingResponseModel>>
    suspend fun setEventTask(token: String,request: SaveEventRequestModel): Flow<DataState<SaveEventResponseModel>>
}