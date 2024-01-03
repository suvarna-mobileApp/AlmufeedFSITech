package com.almufeed.cafm.ui.home.attachment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.repository.BookInfoRepository
import com.almufeed.cafm.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.cafm.datasource.network.models.attachment.AttachmentData
import com.almufeed.cafm.datasource.network.models.attachment.AttachmentRequestModel
import com.almufeed.cafm.datasource.network.models.attachment.AttachmentResponseModel
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

    fun requestForImage(image1 : String, image2 : String, image3 : String,image4 : String,image5 : String, image6 : String,
                        image7 : String,image8 : String,image9 : String,image10 : String,image11 : String,
                        image12 : String,image13 : String,image14 : String,image15 : String,imageType : Int,imageDetail : String,taskId : String) = viewModelScope.launch {
        val userName = basePreferencesManager.getUserName().first()
        val imageRequest = AttachmentData(
            Image1 = image1,
            Image2 = image2,
            Image3 = image3,
            Image4 = image4,
            Image5 = image5,
            Image6 = image6,
            Image7 = image7,
            Image8 = image8,
            Image9 = image9,
            Image10 = image10,
            Image11 = image11,
            Image12 = image12,
            Image13 = image13,
            Image14 = image14,
            Image15 = image15,
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

    suspend fun requestForImageDB(image1 : ByteArray?, image2 : ByteArray?, image3 : ByteArray?, image4 : ByteArray?, image5 : ByteArray?,
                                  image6 : ByteArray?, imageType : Int, imageDetail : String, taskId : String) : AttachmentEntity{
        val userName = basePreferencesManager.getUserName().first()
        var attachmentEntity = AttachmentEntity(
            0,
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
        return attachmentEntity
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