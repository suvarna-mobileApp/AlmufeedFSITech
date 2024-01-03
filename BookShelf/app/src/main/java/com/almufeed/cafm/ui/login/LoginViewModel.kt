package com.almufeed.cafm.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.repository.BookInfoRepository
import com.almufeed.cafm.datasource.network.models.login.LoginRequest
import com.almufeed.cafm.datasource.network.models.login.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val bookInfoRepository: BookInfoRepository,
    private val basePreferencesManager: BasePreferencesManager
) : ViewModel() {

    private val _myLoginDataSTate: MutableLiveData<DataState<LoginResponse>> = MutableLiveData()
    val myLoginDataSTate: LiveData<DataState<LoginResponse>> get() = _myLoginDataSTate

    fun loginRequest(userName : String, password : String) = viewModelScope.launch {
        val loginRequest = LoginRequest(
            _userName = userName,
            _password = password
        )
        setStateEvent(TaskEventLogin.Login(loginRequest))
    }

    private fun setStateEvent(state: TaskEventLogin) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is TaskEventLogin.Login -> {
                    System.out.println("getAccessToken " + basePreferencesManager.getAccessToken().first())
                    bookInfoRepository.login(
                        basePreferencesManager.getAccessToken().first(),state.loginRequest
                    ).onEach {
                        _myLoginDataSTate.value = it
                    }.launchIn(viewModelScope)
                }
            }
        }.exhaustive
    }
}
sealed class TaskEventLogin {
    data class Login(val loginRequest: LoginRequest) : TaskEventLogin()
}