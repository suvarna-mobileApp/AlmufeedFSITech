package com.almufeed.cafm.ui.home

import android.content.Context
import com.almufeed.cafm.datasource.network.retrofit.BookWebServices
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class APIServices {
    companion object {

        var BASE_URL = "https://login.microsoftonline.com/8bd1367c-efa4-40b4-acac-9f3e4c82000b/oauth2/"
        val BASE_URL1 = "https://iye-live.operations.dynamics.com/api/services/FSIMobileServices/FSIMobileService/"
        //var BASE_URL1 = "https://almdevb0bb67faa678fcfadevaos.axcloud.dynamics.com/api/services/FSIMobileServices/FSIMobileService/getTaskList/"
        //var BASE_URL1 = "https://alm.sandbox.operations.eu.dynamics.com/api/services/FSIMobileServices/FSIMobileService/"
        //var BASE_URL1 = "https://ahsca7486d9b32c9b0ddevaos.axcloud.dynamics.com/api/services/FSIMobileServices/FSIMobileService/"

        fun create(): BookWebServices{

            val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(BookWebServices::class.java)
        }

        fun notcreate(context:Context): BookWebServices{
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

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL1)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
            return retrofit.create(BookWebServices::class.java)

            /* val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL1).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(BookWebServices::class.java)*/
        }
    }
}
