package com.almufeed.technician.ui.login.mobile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.technician.business.domain.state.DataState
import com.almufeed.technician.business.domain.utils.exhaustive
import com.almufeed.technician.business.repository.BookInfoRepository
import com.almufeed.technician.datasource.network.models.login.LoginRequest
import com.almufeed.technician.datasource.network.models.login.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MobileViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
) : ViewModel() {

    private val taskEventChannel = Channel<TaskEvent>()
    private val _myLoginDataSTate: MutableLiveData<DataState<LoginResponse>> = MutableLiveData()
    val myLoginDataSTate: LiveData<DataState<LoginResponse>> get() = _myLoginDataSTate

    fun updatePersonalInfo(token: String, userName : String, password : String) = viewModelScope.launch {
        //basePreferencesManager.updateUserMobile(mobileNumber)
        /*if (basePreferencesManager.isNewUser().first()) {
            taskEventChannel.send(TaskEvent.NavigateToPersonalInfo)
        } else {
            taskEventChannel.send(TaskEvent.NavigateToLaunchpad)
        }*/
        val loginRequest = LoginRequest(
            _userName = userName,
            _password = password
        )
        setStateEvent(TaskEvent.Login(loginRequest,token))
    }

    private fun setStateEvent(state: TaskEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEvent.Login -> {
                    bookInfoRepository.login(
                         state.token,state.loginRequest
                    ).onEach {
                        _myLoginDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}

sealed class TaskEvent {
    data class Login(val loginRequest: LoginRequest,val token: String) : TaskEvent()
}