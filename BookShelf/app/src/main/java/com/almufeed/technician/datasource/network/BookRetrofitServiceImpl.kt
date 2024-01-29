package com.almufeed.technician.datasource.network

import android.util.Log
import com.almufeed.technician.business.domain.state.DataState
import com.almufeed.technician.business.domain.state.ErrorUtils
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
import com.almufeed.technician.datasource.network.retrofit.BookWebServices
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.EOFException

class BookRetrofitServiceImpl constructor(
    private val bookWebServices: BookWebServices,
    private val errorUtils: ErrorUtils
) : BookRetrofitService {

    override suspend fun login(token: String,request: LoginRequest): Flow<DataState<LoginResponse>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.login(token,request)
                System.out.println("login response " + response.headers())
                if (response.isSuccessful) {
                    Log.e("AR_SUBSTOP::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.e("AR_SUBSTOP::", "API NOT isSuccessful: ")
                    Log.e("AR_SUBSTOP::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("AR_SUBSTOP::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun getTaskList(token: String,request: TaskListRequest): Flow<DataState<TaskListResponse>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.getTaskList(token,request)
                System.out.println("task request " + request)
                System.out.println("task response " + response.body())
                if (response.isSuccessful) {
                    Log.e("Task::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.e("Task::", "API NOT isSuccessful: ")
                    Log.e("Task::", "ERROR : " + response.code())
                    emit(DataState.Loading(false))
                    if(response.code() == 401){
                        emit(DataState.Error(CancellationException("Authentication failed")))
                    }else{
                        emit(DataState.Error(CancellationException("Some Error. Please try again later")))
                    }
                }
            } catch (e: EOFException) {
                Log.e("Task::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("Authentication failed")))
                //emit(DataState.Error(CancellationException("unknown")))
            }catch (e: Exception) {
                Log.e("Task::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun getProblemList(token: String,request: InstructionSetRequestModel): Flow<DataState<InstructionSetResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.getProblemList(token,request)
                System.out.println("Instruction request " + request)
                System.out.println("Instruction response " + response.body())
                if (response.isSuccessful) {
                    Log.e("Instruction::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.e("Instruction::", "API NOT isSuccessful: ")
                    Log.e("Instruction::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("Instruction::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun updateProblemList(token: String,request: UpdateInstructionSetRequestModel): Flow<DataState<UpdateInstructionSetResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.updateProblemList(token,request)
                System.out.println("update Instruction request " + request)
                System.out.println("update Instruction response " + response.body())
                if (response.isSuccessful) {
                    Log.d("AR_SUBSTOP::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.d("AR_SUBSTOP::", "API NOT isSuccessful: ")
                    Log.d("AR_SUBSTOP::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("AR_SUBSTOP::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun addAttachment(token: String,request: AttachmentRequestModel): Flow<DataState<AttachmentResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.addAttachment(token,request)
                System.out.println("Send image request " + request)
                System.out.println("Send image response " + response.body())
                if (response.isSuccessful) {
                    Log.d("Send image::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.d("Send image::", "API NOT isSuccessful: ")
                    Log.d("Send image::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("Send image::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun getAttachment(token: String,request: GetAttachmentRequestModel): Flow<DataState<GetAttachmentResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.getAttachment(token,request)
                System.out.println("Send image request " + request)
                System.out.println("Send image response " + response.body())
                if (response.isSuccessful) {
                    Log.d("Send image::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.d("Send image::", "API NOT isSuccessful: ")
                    Log.d("Send image::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("Send image::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun setRating(token: String,request: RatingRequestModel): Flow<DataState<RatingResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.setProblemRating(token,request)
                System.out.println("Send rating request " + request)
                System.out.println("Send rating response " + response.body())
                if (response.isSuccessful) {
                    Log.d("Send rating::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.d("Send rating::", "API NOT isSuccessful: ")
                    Log.d("Send rating::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("Send rating::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun setCustomerDetail(token: String,request: CustomerRequestModel): Flow<DataState<CustomerResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.setCustomerDetail(token,request)
                System.out.println("Send customer request " + request.FsiRating)
                System.out.println("Send customer response " + response.body())
                if (response.isSuccessful) {
                    Log.d("Send customer::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.d("Send customer::", "API NOT isSuccessful: ")
                    Log.d("Send customer::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("Send customer::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun getEventList(token: String): Flow<DataState<GetEventListResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.getEventList(token)
                System.out.println("get event response " + response.body())
                if (response.isSuccessful) {
                    Log.d("get event::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.d("get event::", "API NOT isSuccessful: ")
                    Log.d("get event::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("get event::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }

    override suspend fun setEventTask(token: String,request: SaveEventRequestModel): Flow<DataState<SaveEventResponseModel>> =
        flow {
            emit(DataState.Loading(true))
            try {
                val response = bookWebServices.setEventTask(token,request)
                System.out.println("Send event request " + request)
                System.out.println("Send event response " + response.body())
                if (response.isSuccessful) {
                    Log.d("Send event::", "API isSuccessful: ")
                    emit(DataState.Success(response.body()!!))
                    emit(DataState.Loading(false))
                } else {
                    Log.d("Send event::", "API NOT isSuccessful: ")
                    Log.d("Send event::", "ERROR : " + response.errorBody()?.string())
                    emit(DataState.Loading(false))
                    emit(DataState.Error(CancellationException("unknown")))
                }
            } catch (e: Exception) {
                Log.e("Send event::", "Exception: $e.")
                emit(DataState.Loading(false))
                emit(DataState.Error(CancellationException("unknown")))
            }
        }
}