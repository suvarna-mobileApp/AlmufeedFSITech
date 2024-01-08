package com.almufeed.cafm.ui.home.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.repository.BookInfoRepository
import com.almufeed.cafm.datasource.network.models.customer.CustomerData
import com.almufeed.cafm.datasource.network.models.customer.CustomerRequestModel
import com.almufeed.cafm.datasource.network.models.customer.CustomerResponseModel
import com.almufeed.cafm.datasource.network.models.events.GetEventListResponseModel
import com.almufeed.cafm.datasource.network.models.events.SaveEventData
import com.almufeed.cafm.datasource.network.models.events.SaveEventRequestModel
import com.almufeed.cafm.datasource.network.models.events.SaveEventResponseModel
import com.almufeed.cafm.datasource.network.models.rating.RatingData
import com.almufeed.cafm.datasource.network.models.rating.RatingRequestModel
import com.almufeed.cafm.datasource.network.models.rating.RatingResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProofOfAttendenceViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val _myRateDataSTate: MutableLiveData<DataState<CustomerResponseModel>> = MutableLiveData()
    val myRateDataSTate: LiveData<DataState<CustomerResponseModel>> get() = _myRateDataSTate

    fun requestForCustomer(customerName : String, Email:String,Phone: String,
                         taskId: String) = viewModelScope.launch {
        val userName = basePreferencesManager.getUserName().first()
        val taskRequest = CustomerData(
            customerName = customerName,
            Email = Email,
            Phone = Phone,
            taskId = taskId
        )
        val update = CustomerRequestModel(
            FsiRating = taskRequest
        )
        setStateEvent(TaskEvent.Task(update))
    }

    private fun setStateEvent(state: TaskEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEvent.Task -> {
                    bookInfoRepository.setCustomerDetail(
                        basePreferencesManager.getAccessToken().first(),state.customerRequest
                    ).onEach {
                        _myRateDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class TaskEvent {
    data class Task(val customerRequest: CustomerRequestModel) : TaskEvent()
}