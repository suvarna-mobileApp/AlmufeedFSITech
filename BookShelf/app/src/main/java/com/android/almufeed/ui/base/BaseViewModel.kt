package com.android.almufeed.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.almufeed.business.domain.utils.dataStore.BasePreferencesManager
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.di.NetworkConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    private val basePreferencesManager: BasePreferencesManager,
    @NetworkConnection private val connectionLiveData: LiveData<Boolean>
) : ViewModel() {

    val isNetworkConnected = connectionLiveData

    fun setToken(token : String) {
        setStateEvent(Event.ResetToken(token))
    }

    fun updateLogin() = viewModelScope.launch {
        basePreferencesManager.setUserLogin(true)
        System.out.println("user first1 " + basePreferencesManager.isUserLogIn().first())
    }

    fun updateUsername(userName:String) = viewModelScope.launch {
        basePreferencesManager.updateUserName(userName)
    }

    private fun setStateEvent(state: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            when (state) {
                is Event.ResetToken -> {
                    basePreferencesManager.updateAccessToken(state.token)
                }
            }
        }.exhaustive
    }
}

sealed class Event {
    data class ResetToken(val token: String): Event()
}