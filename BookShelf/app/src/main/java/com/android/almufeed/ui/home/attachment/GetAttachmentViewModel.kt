package com.android.almufeed.ui.home.attachment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.dataStore.BasePreferencesManager
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.business.repository.BookInfoRepository
import com.android.almufeed.datasource.network.models.attachment.AttachmentRequestModel
import com.android.almufeed.datasource.network.models.attachment.GetAttachmentRequestModel
import com.android.almufeed.datasource.network.models.attachment.GetAttachmentResponseModel
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
class GetAttachmentViewModel  @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val _myImageDataSTate: MutableLiveData<DataState<GetAttachmentResponseModel>> = MutableLiveData()
    val myImageDataSTate: LiveData<DataState<GetAttachmentResponseModel>> get() = _myImageDataSTate

    fun requestForImage(taskId : String) = viewModelScope.launch {
        val taskId = GetAttachmentRequestModel(
            taskId = taskId
        )
        setStateEvent(AttachmentEvent.ImageTask(taskId))
    }

    private fun setStateEvent(state: AttachmentEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is AttachmentEvent.ImageTask -> {
                    bookInfoRepository.getAttachment(
                        basePreferencesManager.getAccessToken().first(),state.taskId
                    ).onEach {
                        _myImageDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class AttachmentEvent {
    data class ImageTask(val taskId: GetAttachmentRequestModel) : AttachmentEvent()
}