package com.almufeed.cafm.ui.home.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.repository.BookInfoRepository
import com.almufeed.cafm.datasource.network.models.events.GetEventListResponseModel
import com.almufeed.cafm.datasource.network.models.events.SaveEventData
import com.almufeed.cafm.datasource.network.models.events.SaveEventRequestModel
import com.almufeed.cafm.datasource.network.models.events.SaveEventResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEventsViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val _myEventDataSTate: MutableLiveData<DataState<GetEventListResponseModel>> = MutableLiveData()
    val myEventDataSTate: LiveData<DataState<GetEventListResponseModel>> get() = _myEventDataSTate

    private val _mySetEventDataSTate: MutableLiveData<DataState<SaveEventResponseModel>> = MutableLiveData()
    val mySetEventDataSTate: LiveData<DataState<SaveEventResponseModel>> get() = _mySetEventDataSTate

    fun requestForEvent() = viewModelScope.launch {
        setEvent()
    }
    fun saveForEvent(taskId : String, comments : String, events : String) = viewModelScope.launch {
        val userName = basePreferencesManager.getUserName().first()
        val imageRequest = SaveEventData(
            taskId = taskId,
            resource = userName,
            Comments = comments,
            Events = events
        )
        val update = SaveEventRequestModel(
            fsiImage = imageRequest
        )
        setStateEvent(TaskEvent.SaveEvent(update))
    }

    private fun setEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            bookInfoRepository.getEventList(
                basePreferencesManager.getAccessToken().first()
            ).onEach {
                _myEventDataSTate.value = it
            }.launchIn(viewModelScope)
        }.exhaustive
    }

    private fun setStateEvent(state: TaskEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEvent.SaveEvent -> {
                    bookInfoRepository.setEvent(
                        basePreferencesManager.getAccessToken().first(),state.Request
                    ).onEach {
                        _mySetEventDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class TaskEvent {
    data class SaveEvent(val Request: SaveEventRequestModel) : TaskEvent()
}