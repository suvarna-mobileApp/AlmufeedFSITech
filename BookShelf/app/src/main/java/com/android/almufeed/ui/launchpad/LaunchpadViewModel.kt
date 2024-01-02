package com.android.almufeed.ui.launchpad

import androidx.lifecycle.ViewModel
import com.android.almufeed.business.domain.utils.dataStore.BasePreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LaunchpadViewModel @Inject constructor(
    private val basePreferencesManager: BasePreferencesManager,
) : ViewModel() {

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()
}

sealed class TaskEvent {
    data class SetUserDetails(
        val name: String,
        val mobileNumber: String,
        val email: String,
        val userProfilePic: String,
    ) : TaskEvent()
}