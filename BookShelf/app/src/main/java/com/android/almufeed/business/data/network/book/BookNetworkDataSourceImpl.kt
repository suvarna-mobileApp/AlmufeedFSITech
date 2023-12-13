package com.android.almufeed.business.data.network.book

import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.datasource.network.BookRetrofitService
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

class BookNetworkDataSourceImpl @Inject constructor(
    private val bookRetrofitService: BookRetrofitService,
    ): BookNetworkDataSource {

    override suspend fun login(token: String,request: LoginRequest): Flow<DataState<LoginResponse>> = flow{
        bookRetrofitService.login(token,request).collect{ plans ->
            emit(plans)
        }
    }

    override suspend fun taskList(token: String,request: TaskListRequest): Flow<DataState<TaskListResponse>> = flow{
        bookRetrofitService.getTaskList(token,request).collect{ plans ->
            emit(plans)
        }
    }
    override suspend fun getProblemList(token: String,request: InstructionSetRequestModel): Flow<DataState<InstructionSetResponseModel>> = flow{
        bookRetrofitService.getProblemList(token,request).collect{ plans ->
            emit(plans)
        }
    }
    override suspend fun updateProblemList(token: String,request: UpdateInstructionSetRequestModel): Flow<DataState<UpdateInstructionSetResponseModel>> = flow{
        bookRetrofitService.updateProblemList(token,request).collect{ plans ->
            emit(plans)
        }
    }

    override suspend fun addAttachment(token: String,request: AttachmentRequestModel): Flow<DataState<AttachmentResponseModel>> = flow{
        bookRetrofitService.addAttachment(token,request).collect{ plans ->
            emit(plans)
        }
    }

    override suspend fun getAttachment(token: String,request: GetAttachmentRequestModel): Flow<DataState<GetAttachmentResponseModel>> = flow{
        bookRetrofitService.getAttachment(token,request).collect{ plans ->
            emit(plans)
        }
    }

    override suspend fun getEventList(token: String): Flow<DataState<GetEventListResponseModel>> = flow{
        bookRetrofitService.getEventList(token).collect{ plans ->
            emit(plans)
        }
    }
    override suspend fun setRating(token: String,request: RatingRequestModel): Flow<DataState<RatingResponseModel>> = flow{
        bookRetrofitService.setRating(token,request).collect{ plans ->
            emit(plans)
        }
    }

    override suspend fun setEventTask(token: String,request: SaveEventRequestModel): Flow<DataState<SaveEventResponseModel>> = flow{
        bookRetrofitService.setEventTask(token,request).collect{ plans ->
            emit(plans)
        }
    }
}