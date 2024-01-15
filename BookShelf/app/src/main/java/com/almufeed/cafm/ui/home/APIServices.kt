package com.almufeed.cafm.ui.home
//PM-ART-1-Task-7
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.almufeed.cafm.datasource.network.models.token.CreateTokenResponse
import com.almufeed.cafm.datasource.network.retrofit.BookWebServices
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        var mediaType = "application/x-www-form-urlencoded"
        var body = "client_id=7d2f26f6-2e67-4299-9abd-fbac27deff25&client_secret=rcI8Q~eugdoR2M0Yx8_gkTPqqyPyT.sn9ab3BdeF&grant_type=client_credentials&resource=https://iye-live.operations.dynamics.com"

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
