package com.android.almufeed.business.repository

import com.android.almufeed.business.data.network.book.BookNetworkDataSource
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

    suspend fun setEvent(token: String,request: SaveEventRequestModel): Flow<DataState<SaveEventResponseModel>> =
        flow {
            bookNetworkDataSource.setEventTask(token,request).collect {
                emit(it)
            }
        }
}