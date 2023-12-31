package com.almufeed.cafm.business.domain.utils.dataStore

import kotlinx.coroutines.flow.Flow

interface BasePreferencesManager {
    suspend fun getAccessToken(): Flow<String>
    suspend fun updateAccessToken(accessToken: String)
    suspend fun isUserLogIn(): Flow<Boolean>
    suspend fun setUserLogin(role: Boolean)
    suspend fun getUserName(): Flow<String>
    suspend fun updateUserName(userName: String)

}