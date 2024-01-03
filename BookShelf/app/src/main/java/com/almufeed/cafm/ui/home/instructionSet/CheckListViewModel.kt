package com.almufeed.cafm.ui.home.instructionSet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.repository.BookInfoRepository
import com.almufeed.cafm.datasource.network.models.instructionSet.InstructionSetRequestModel
import com.almufeed.cafm.datasource.network.models.instructionSet.InstructionSetResponseModel
import com.almufeed.cafm.datasource.network.models.updateInstruction.UpdateInstructionData
import com.almufeed.cafm.datasource.network.models.updateInstruction.UpdateInstructionSetRequestModel
import com.almufeed.cafm.datasource.network.models.updateInstruction.UpdateInstructionSetResponseModel
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
class CheckListViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val _myTaskDataSTate: MutableLiveData<DataState<InstructionSetResponseModel>> = MutableLiveData()
    val myTaskDataSTate: LiveData<DataState<InstructionSetResponseModel>> get() = _myTaskDataSTate

    private val _myUpdateDataSTate: MutableLiveData<DataState<UpdateInstructionSetResponseModel>> = MutableLiveData()
    val myUpdateDataSTate: LiveData<DataState<UpdateInstructionSetResponseModel>> get() = _myUpdateDataSTate

    fun requestForStep(resourceId : String) = viewModelScope.launch {
        val taskRequest = InstructionSetRequestModel(
            _ID = resourceId
        )
        setStateEvent(TaskEvent.Task(taskRequest))
    }

    fun updateForStep(refId : Long, feedBackType : Int, answer : String, taskId : String) = viewModelScope.launch {
        val taskRequest = UpdateInstructionData(
            Refrecid = refId,
            FeedbackType = feedBackType,
            AnswerSet = answer,
            taskId = taskId
        )
        val update = UpdateInstructionSetRequestModel(
            problem = taskRequest
        )
        setStateEvent(TaskEvent.UpdateTask(update))
    }

    private fun setStateEvent(state: TaskEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEvent.Task -> {
                    bookInfoRepository.problemList(
                        basePreferencesManager.getAccessToken().first(),state.stepRequest
                    ).onEach {
                        _myTaskDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
                is TaskEvent.UpdateTask -> {
                    bookInfoRepository.updateProblemList(
                        basePreferencesManager.getAccessToken().first(),state.stepRequest
                    ).onEach {
                        _myUpdateDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class TaskEvent {
    data class Task(val stepRequest: InstructionSetRequestModel) : TaskEvent()
    data class UpdateTask(val stepRequest: UpdateInstructionSetRequestModel) : TaskEvent()
}