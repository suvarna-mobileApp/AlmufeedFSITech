package com.almufeed.cafm.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.di.NetworkConnection
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
    suspend fun getUsername() : String{
        val userName = basePreferencesManager.getUserName().first()
        return userName
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