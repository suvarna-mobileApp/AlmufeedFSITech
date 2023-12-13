package com.android.almufeed.ui.login.mobile

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.almufeed.R
import com.android.almufeed.business.domain.state.DataState
import com.android.almufeed.business.domain.utils.collectLatestFlow
import com.android.almufeed.business.domain.utils.errorListener
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.databinding.FragmentMobileBinding
import com.android.almufeed.datasource.network.models.token.CreateTokenResponse
import com.android.almufeed.ui.home.APIServices
import com.android.almufeed.ui.launchpad.DashboardActivity
import com.android.almufeed.ui.launchpad.LaunchpadActivity
import com.android.almufeed.ui.personalInfo.PersonalInfoActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class MobileFragment : Fragment(R.layout.fragment_mobile) {

    private val mobileViewModel: MobileViewModel by viewModels()
    private lateinit var binding: FragmentMobileBinding
    private lateinit var accessToken : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMobileBinding.bind(view)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        binding.apply {

            cardMobileNumber.errorListener(binding.etusername)
            cardPassword.errorListener(binding.etpassword)
            progressBar.visibility = View.INVISIBLE

            /*etMobile.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int, p2: Int, p3: Int
                ) {
                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int, p2: Int, p3: Int
                ) {
                    // check inputted characters is a valid phone number or not
                    if (p0?.length!! < 5) {
                        mobileViewModel.valid = true
                        etMobile.error = null
                    } else {
                        mobileViewModel.valid = false
                        // etMobile.error = "Invalid phone number."
                    }
                    *//*if (p0?.length!! < 11) {
                        mobileViewModel.valid = true
                        etMobile.error = null
                    } else {
                        mobileViewModel.valid = false
                        // etMobile.error = "Invalid phone number."
                    }*//*
                }

                override fun afterTextChanged(p0: Editable?) {}
            })*/
            btnSignIn.setOnClickListener {

                if(binding.etusername.text.isNullOrEmpty()){
                    binding.cardMobileNumber.error = "Please enter a user name"
                }else if(binding.etpassword.text.isNullOrEmpty()){
                    binding.cardPassword.error = "Please enter a password"
                }else if(binding.etusername.text.toString().isNotEmpty() && binding.etpassword.text.toString().isNotEmpty()){
                    binding.progressBar.visibility = View.VISIBLE
                    getTokenApi(binding.etusername.text!!.trim().toString(), binding.etpassword.text!!.trim().toString())
                }
            }
        }
        subscribeObservers()
    }

    private fun getTokenApi(userName : String, password : String) {

        /* val client = OkHttpClient()
         val mediaType = "application/x-www-form-urlencoded".toMediaType()
         val body = "client_id=7d2f26f6-2e67-4299-9abd-fbac27deff25&client_secret=rcI8Q~eugdoR2M0Yx8_gkTPqqyPyT.sn9ab3BdeF&grant_type=client_credentials&resource=https://almdevb0bb67faa678fcfadevaos.axcloud.dynamics.com".toRequestBody(mediaType)
         val request = Request.Builder()
             .url("https://login.microsoftonline.com/8bd1367c-efa4-40b4-acac-9f3e4c82000b/oauth2/token")
             .post(body)
             .addHeader("Content-Type", "application/x-www-form-urlencoded")
             .addHeader("Cookie", "fpc=AhhlLpiHrg1Lq-3EvIUzBvlCIhD-AQAAAF195twOAAAA; stsservicecookie=estsfd; x-ms-gateway-slice=estsfd")
             .build()*/

        //val getToken = client.newCall(request)
        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val body = "client_id=7d2f26f6-2e67-4299-9abd-fbac27deff25&client_secret=rcI8Q~eugdoR2M0Yx8_gkTPqqyPyT.sn9ab3BdeF&grant_type=client_credentials&resource=https://almdevb0bb67faa678fcfadevaos.axcloud.dynamics.com".toRequestBody(mediaType)
        val getToken = APIServices.create().getProducts(body)
        getToken.enqueue(object : Callback<CreateTokenResponse> {
            override fun onResponse(
                call: Call<CreateTokenResponse>,
                response: Response<CreateTokenResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("products", response.body().toString())
                    if (response.body() != null) {
                        accessToken = "Bearer " + response.body()!!.access_token
                        mobileViewModel.updatePersonalInfo(accessToken,userName,password)
                    }
                }
            }

            override fun onFailure(call: Call<CreateTokenResponse>, t: Throwable) {
                Log.d("failure", t.message.toString())
                Toast.makeText(
                    requireActivity(),
                    "onFailure: " + t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


        /* val requestBody = CreateTokenRequest("7d2f26f6-2e67-4299-9abd-fbac27deff25", "rcI8Q~eugdoR2M0Yx8_gkTPqqyPyT.sn9ab3BdeF","client_credentials","https://almdevb0bb67faa678fcfadevaos.axcloud.dynamics.com")
          System.out.println("request1 " + requestBody)
          val mediaType = "application/x-www-form-urlencoded".toMediaType()
          val body = "client_id=7d2f26f6-2e67-4299-9abd-fbac27deff25&client_secret=rcI8Q~eugdoR2M0Yx8_gkTPqqyPyT.sn9ab3BdeF&grant_type=client_credentials&resource=https://almdevb0bb67faa678fcfadevaos.axcloud.dynamics.com".toRequestBody(mediaType)
          try {
              val getToken = APIServices.create().getProducts(body)
              System.out.println("request2 " + getToken.request() + " " + getToken.isExecuted)
              getToken.enqueue(object : Callback<CreateTokenResponse> {
                  override fun onResponse(
                      call: Call<CreateTokenResponse>,
                      response: Response<CreateTokenResponse>
                  ) {
                      if (response.isSuccessful) {
                          Log.d("products", response.body()!!.access_token)
                          if (response.body() != null) {

                          }
                      }
                  }

                  override fun onFailure(call: Call<CreateTokenResponse>, t: Throwable) {
                      Log.d("failure", t.message.toString())
                      Toast.makeText(
                          this@DashboardActivity,
                          "onFailure: " + t.message,
                          Toast.LENGTH_SHORT
                      ).show()
                  }
              })*/
        /*  }catch(e:Exception){
              e.printStackTrace()
          }*/
    }

    private fun subscribeObservers() {
        mobileViewModel.myLoginDataSTate.observe(requireActivity()) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    Toast.makeText(requireActivity(),"Something went wrong",Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {
                    //displayProgressBar(dataState.loading)
                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    gotoLaunchpadPage(dataState.data.ResourceID)
                }
            }.exhaustive
        }
    }

    private fun gotoLaunchpadPage(resourceId:String) {
        Intent(requireActivity(), DashboardActivity::class.java).apply {
            putExtra("resourceId",resourceId)
            putExtra("token",accessToken)
            startActivity(this)
        }
    }
}


