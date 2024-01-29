package com.almufeed.technician.business.data.network.book

import com.almufeed.technician.business.domain.state.DataState
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
import com.almufeed.technician.datasource.network.models.updateInstruction.UpdateInstructionSetRequestModel
import com.almufeed.technician.datasource.network.models.updateInstruction.UpdateInstructionSetResponseModel
import kotlinx.coroutines.flow.Flow

interface BookNetworkDataSource {
    suspend fun login(token: String,request: LoginRequest): Flow<DataState<LoginResponse>>
    suspend fun taskList(token: String,request: TaskListRequest): Flow<DataState<TaskListResponse>>
    suspend fun getProblemList(token: String,request: InstructionSetRequestModel): Flow<DataState<InstructionSetResponseModel>>
    suspend fun updateProblemList(token: String,request: UpdateInstructionSetRequestModel): Flow<DataState<UpdateInstructionSetResponseModel>>
    suspend fun addAttachment(token: String,request: AttachmentRequestModel): Flow<DataState<AttachmentResponseModel>>
    suspend fun getAttachment(token: String,request: GetAttachmentRequestModel): Flow<DataState<GetAttachmentResponseModel>>
    suspend fun getEventList(token: String): Flow<DataState<GetEventListResponseModel>>
    suspend fun setRating(token: String,request: RatingRequestModel): Flow<DataState<RatingResponseModel>>
    suspend fun setCustomerDetail(token: String,request: CustomerRequestModel): Flow<DataState<CustomerResponseModel>>
    suspend fun setEventTask(token: String,request: SaveEventRequestModel): Flow<DataState<SaveEventResponseModel>>
}