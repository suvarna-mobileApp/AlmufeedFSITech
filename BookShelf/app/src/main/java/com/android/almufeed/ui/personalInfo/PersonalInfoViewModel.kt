package com.android.almufeed.ui.personalInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.almufeed.business.domain.utils.dataStore.BasePreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {
    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    fun navigateToLaunchpad(firstNmae: String, lastName: String, email : String, imageurl: String) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToDashboard)
    }
    sealed class TaskEvent {
        object NavigateToDashboard : TaskEvent()
    }
}