package com.almufeed.technician.business.repository

import com.almufeed.technician.business.data.network.book.BookNetworkDataSource
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
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookInfoRepository @Inject constructor(
    private val bookNetworkDataSource: BookNetworkDataSource
) {
        suspend fun login(token: String,request: LoginRequest): Flow<DataState<LoginResponse>> =
            flow {
                bookNetworkDataSource.login(token,request).collect {
                    emit(it)
                }
            }

    suspend fun taskList(token: String,request: TaskListRequest): Flow<DataState<TaskListResponse>> =
        flow {
            bookNetworkDataSource.taskList(token,request).collect {
                emit(it)
            }
        }

    suspend fun problemList(token: String,request: InstructionSetRequestModel): Flow<DataState<InstructionSetResponseModel>> =
        flow {
            bookNetworkDataSource.getProblemList(token,request).collect {
                emit(it)
            }
        }

    suspend fun updateProblemList(token: String,request: UpdateInstructionSetRequestModel): Flow<DataState<UpdateInstructionSetResponseModel>> =
        flow {
            bookNetworkDataSource.updateProblemList(token,request).collect {
                emit(it)
            }
        }

    suspend fun addAttachment(token: String,request: AttachmentRequestModel): Flow<DataState<AttachmentResponseModel>> =
        flow {
            bookNetworkDataSource.addAttachment(token,request).collect {
                emit(it)
            }
        }

    suspend fun getAttachment(token: String,request: GetAttachmentRequestModel): Flow<DataState<GetAttachmentResponseModel>> =
        flow {
            bookNetworkDataSource.getAttachment(token,request).collect {
                emit(it)
            }
        }
    suspend fun getEventList(token: String): Flow<DataState<GetEventListResponseModel>> =
        flow {
            bookNetworkDataSource.getEventList(token).collect {
                emit(it)
            }
        }
    suspend fun setRating(token: String,request: RatingRequestModel): Flow<DataState<RatingResponseModel>> =
        flow {
            bookNetworkDataSource.setRating(token,request).collect {
                emit(it)
            }
        }

    suspend fun setCustomerDetail(token: String,request: CustomerRequestModel): Flow<DataState<CustomerResponseModel>> =
        flow {
            bookNetworkDataSource.setCustomerDetail(token,request).collect {
                emit(it)
            }
        }

    suspend fun setEvent(token: String,request: SaveEventRequestModel): Flow<DataState<SaveEventResponseModel>> =
        flow {
            bookNetworkDataSource.setEventTask(token,request).collect {
                emit(it)
            }
        }
}