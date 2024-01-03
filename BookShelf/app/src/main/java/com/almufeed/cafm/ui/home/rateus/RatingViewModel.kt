package com.almufeed.cafm.ui.home.rateus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.repository.BookInfoRepository
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
class RatingViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val _myRateDataSTate: MutableLiveData<DataState<RatingResponseModel>> = MutableLiveData()
    val myRateDataSTate: LiveData<DataState<RatingResponseModel>> get() = _myRateDataSTate

    fun requestForRating(customerSignature : String, techiSignature:String,rating: Double,comment: String,dateTime: String,
                         taskId: String,name:String,email:String,mobilenumber:String) = viewModelScope.launch {
        val userName = basePreferencesManager.getUserName().first()
            val taskRequest = RatingData(
            customerSignature = customerSignature,
                customerName = name,
                Email = email,
                Phone = mobilenumber,
            techiSignature = techiSignature,
            rating = rating,
            comment = comment,
            dateTime = dateTime,
            taskId = taskId,
            resource = userName
        )
        val update = RatingRequestModel(
            FsiRating = taskRequest
        )
        setStateEvent(TaskEvent.Task(update))
    }

    private fun setStateEvent(state: TaskEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEvent.Task -> {
                    bookInfoRepository.setRating(
                        basePreferencesManager.getAccessToken().first(),state.stepRequest
                    ).onEach {
                        _myRateDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class TaskEvent {
    data class Task(val stepRequest: RatingRequestModel) : TaskEvent()
}