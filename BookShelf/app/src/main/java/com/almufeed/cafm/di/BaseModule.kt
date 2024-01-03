package com.almufeed.cafm.di

import android.content.Context
import androidx.lifecycle.LiveData
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManagerImpl
import com.almufeed.cafm.business.domain.utils.network.ConnectionLiveData
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseModule {
    @Singleton
    @Provides
    fun provideBasePreferencesManager(@ApplicationContext context: Context): BasePreferencesManager {
        return BasePreferencesManagerImpl(context)
    }

    @Singleton
    @Provides
    fun provideGlide(@ApplicationContext context: Context) = Glide.with(context)

    @NetworkConnection
    @Singleton
    @Provides
    fun provideConnectionLiveData(@ApplicationContext context: Context): LiveData<Boolean> {
        return ConnectionLiveData(context)
    }

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkConnection
