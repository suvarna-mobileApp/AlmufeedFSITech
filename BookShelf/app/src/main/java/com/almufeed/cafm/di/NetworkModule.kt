package com.almufeed.cafm.di

import android.content.Context
import com.almufeed.cafm.business.data.network.book.BookNetworkDataSource
import com.almufeed.cafm.business.data.network.book.BookNetworkDataSourceImpl
import com.almufeed.cafm.business.domain.state.ErrorUtils
import com.almufeed.cafm.business.domain.utils.networkException.ConnectivityInterceptor
import com.almufeed.cafm.datasource.network.BookRetrofitService
import com.almufeed.cafm.datasource.network.BookRetrofitServiceImpl
import com.almufeed.cafm.datasource.network.retrofit.BookWebServices
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @AppRetrofit
    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        gson: Gson,
        @ApplicationContext context: Context
    ): Retrofit.Builder{

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(
                com.almufeed.cafm.business.domain.utils.networkException.ConnectivityInterceptor(
                    context
                )
            )
            .addInterceptor(logging)
            .build()

        //val BASE_URL = "https://almdevb0bb67faa678fcfadevaos.axcloud.dynamics.com/api/services/FSIMobileServices/FSIMobileService/"
        //val BASE_URL = "https://alm.sandbox.operations.eu.dynamics.com/api/services/FSIMobileServices/FSIMobileService/"
        val BASE_URL = "https://iye-live.operations.dynamics.com/api/services/FSIMobileServices/FSIMobileService/"
        //val BASE_URL = "https://ahsca7486d9b32c9b0ddevaos.axcloud.dynamics.com/api/services/FSIMobileServices/FSIMobileService/"
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideRetrofit(@AppRetrofit retrofit: Retrofit.Builder): Retrofit {
        return retrofit.build()
    }

    @Singleton
    @Provides
    fun provideErrorUtils(retrofit: Retrofit): ErrorUtils {
        return ErrorUtils(retrofit)
    }

    @Singleton
    @Provides
    fun provideBookNetworkDataSource(
        bookRetrofitService: BookRetrofitService,
    ): BookNetworkDataSource {
        return BookNetworkDataSourceImpl(
            bookRetrofitService
        )
    }

    @Singleton
    @Provides
    fun provideBookService(@AppRetrofit retrofit: Retrofit.Builder): BookWebServices {
        return retrofit
            .build()
            .create(BookWebServices::class.java)
    }

    @Singleton
    @Provides
    fun provideBookRetrofitService(
        bookWebServices: BookWebServices,
        errorUtils: ErrorUtils
    ): BookRetrofitService {
        return BookRetrofitServiceImpl(bookWebServices, errorUtils)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppRetrofit