package com.android.almufeed.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.dataStore.BasePreferencesManager
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.business.repository.BookInfoRepository
import com.android.almufeed.datasource.network.models.bookList.BookData
import com.android.almufeed.datasource.network.models.bookList.BookListNetworkResponse
import com.android.almufeed.datasource.network.models.login.LoginRequest
import com.android.almufeed.datasource.network.models.login.LoginResponse
import com.android.almufeed.datasource.network.models.tasklist.TaskListRequest
import com.android.almufeed.datasource.network.models.tasklist.TaskListResponse
import com.android.almufeed.datasource.network.models.token.CreateTokenRequest
import com.android.almufeed.datasource.network.models.token.CreateTokenResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val taskEventChannel = Channel<com.android.almufeed.ui.login.mobile.TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val _myTaskDataSTate: MutableLiveData<DataState<TaskListResponse>> = MutableLiveData()
    val myTaskDataSTate: LiveData<DataState<TaskListResponse>> get() = _myTaskDataSTate

    fun requestForTask() = viewModelScope.launch {
        val resourceId = basePreferencesManager.getUserName().first()
        val taskRequest = TaskListRequest(
            _ID = resourceId
        )
        setStateEvent(TaskEvent.Task(taskRequest))
    }

    private fun setStateEvent(state: TaskEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEvent.Task -> {
                    bookInfoRepository.taskList(
                        basePreferencesManager.getAccessToken().first(),state.taskRequest
                    ).onEach {
                        _myTaskDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class TaskEvent {
    data class Task(val taskRequest: TaskListRequest) : TaskEvent()
}