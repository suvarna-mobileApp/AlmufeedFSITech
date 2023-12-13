package com.android.almufeed.ui.home.attachment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.dataStore.BasePreferencesManager
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.business.repository.BookInfoRepository
import com.android.almufeed.datasource.network.models.attachment.AttachmentData
import com.android.almufeed.datasource.network.models.attachment.AttachmentRequestModel
import com.android.almufeed.datasource.network.models.attachment.AttachmentResponseModel
import com.android.almufeed.datasource.network.models.instructionSet.InstructionSetRequestModel
import com.android.almufeed.datasource.network.models.instructionSet.InstructionSetResponseModel
import com.android.almufeed.datasource.network.models.updateInstruction.UpdateInstructionData
import com.android.almufeed.datasource.network.models.updateInstruction.UpdateInstructionSetRequestModel
import com.android.almufeed.datasource.network.models.updateInstruction.UpdateInstructionSetResponseModel
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
class AddAttachmentViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val _myImageDataSTate: MutableLiveData<DataState<AttachmentResponseModel>> = MutableLiveData()
    val myImageDataSTate: LiveData<DataState<AttachmentResponseModel>> get() = _myImageDataSTate

    fun requestForImage(image1 : String, image2 : String, image3 : String,image4 : String,image5 : String, image6 : String,imageType : Int,imageDetail : String,taskId : String) = viewModelScope.launch {
        val userName = basePreferencesManager.getUserName().first()
        val imageRequest = AttachmentData(
            Image1 = image1,
            Image2 = image2,
            Image3 = image3,
            Image4 = image4,
            Image5 = image5,
            Image6 = image6,
            type = imageType,
            description = imageDetail,
            taskId = taskId,
            resource = userName
        )
        val update = AttachmentRequestModel(
            fsiImage = imageRequest
        )
        setStateEvent(TaskEvent.ImageTask(update))
    }

    private fun setStateEvent(state: TaskEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEvent.ImageTask -> {
                    bookInfoRepository.addAttachment(
                        basePreferencesManager.getAccessToken().first(),state.imageRequest
                    ).onEach {
                        _myImageDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class TaskEvent {
    data class ImageTask(val imageRequest: AttachmentRequestModel) : TaskEvent()
}