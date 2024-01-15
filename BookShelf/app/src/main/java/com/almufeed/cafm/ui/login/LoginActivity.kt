package com.almufeed.cafm.ui.login

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.almufeed.cafm.R
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.errorListener
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.domain.utils.isOnline
import com.almufeed.cafm.business.domain.utils.toast
import com.almufeed.cafm.databinding.ActivityLoginBinding
import com.almufeed.cafm.datasource.network.models.token.CreateTokenResponse
import com.almufeed.cafm.ui.base.BaseInterface
import com.almufeed.cafm.ui.base.BaseViewModel
import com.almufeed.cafm.ui.home.APIServices
import com.almufeed.cafm.ui.launchpad.DashboardActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), BaseInterface {

    private val loginViewModel: LoginViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var snack: Snackbar
    private lateinit var pd : Dialog
    private val ACTION_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        baseViewModel.isNetworkConnected.observe(this) { isNetworkAvailable ->
            showNetworkSnackBar(isNetworkAvailable)
        }

        pd = Dialog(this, android.R.style.Theme_Black)
        val view: View = LayoutInflater.from(this).inflate(R.layout.remove_border, null)
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pd.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        pd.setContentView(view)

        binding.apply {
            snack = Snackbar.make(root, getString(R.string.text_network_not_available), Snackbar.LENGTH_INDEFINITE)
        }

        binding.apply {
            toolbar.aboutus.visibility = View.GONE
            cardMobileNumber.errorListener(binding.etusername)
            cardPassword.errorListener(binding.etpassword)

            btnSignIn.setOnClickListener{

                if(binding.etusername.text.isNullOrEmpty()){
                    binding.cardMobileNumber.error = "Please enter a user name"
                }else if(binding.etpassword.text.isNullOrEmpty()){
                    binding.cardPassword.error = "Please enter a password"
                }else if(binding.etusername.text.toString().isNotEmpty() && binding.etpassword.text.toString().isNotEmpty()){
                        if (isValidPassword(binding.etpassword.text.toString())) {
                            if(isOnline(this@LoginActivity)){
                                pd.show()
                                getTokenApi(binding.etusername.text!!.trim().toString(), binding.etpassword.text!!.trim().toString())
                            }else{
                                Toast.makeText(this@LoginActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            binding.cardPassword.error = "Please enter correct password"
                        }
                }
            }
        }
        subscribeObservers()
    }

    private val notificationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Handle the result if needed
        }

    private fun requestNotificationPermission() {
        // Check if the device is running Android 12 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Intent to open the specific app's notification settings
            val intent = Intent(ACTION_NOTIFICATION_SETTINGS).apply {
                putExtra("android.provider.extra.APP_PACKAGE", packageName)
            }

            // Launch the notification settings activity
            notificationSettingsLauncher.launch(intent)
        }
    }

    internal fun isValidPassword(password: String): Boolean {
        //if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        //if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        //if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false
        return true
    }

    private fun getTokenApi(userName : String, password : String) {
        val mediaType = APIServices.mediaType.toMediaType()
        val body = APIServices.body.toRequestBody(mediaType)
        val getToken = APIServices.create().getProducts(body)
        Log.d("products getToken", getToken.request().body.toString())
        try{
            getToken.enqueue(object : Callback<CreateTokenResponse> {
                override fun onResponse(
                    call: Call<CreateTokenResponse>,
                    response: Response<CreateTokenResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("products", response.body().toString())
                        if (response.body() != null) {
                            var accessToken = "Bearer " + response.body()!!.access_token
                            baseViewModel.setToken(response.body()!!.access_token)
                            val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                            val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                            myEdit.putString("token", accessToken)
                            myEdit.commit()
                            //accessToken = "Bearer " + response.body()!!.access_token
                            Handler(Looper.getMainLooper()).postDelayed({
                                loginViewModel.loginRequest(userName,password)
                            }, 1000)
                        }
                    }
                }

                override fun onFailure(call: Call<CreateTokenResponse>, t: Throwable) {
                    Log.d("failure", t.message.toString())
                    Toast.makeText(this@LoginActivity, "onFailure: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }catch(e:Exception){
            e.printStackTrace()
            pd.dismiss()
            Toast.makeText(this@LoginActivity, "Some error. Please try later", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subscribeObservers() {
        loginViewModel.myLoginDataSTate.observe(this@LoginActivity) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    pd.dismiss()
                    Toast.makeText(this@LoginActivity,"Some error. Please try later", Toast.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {
                    //displayProgressBar(dataState.loading)
                }
                is DataState.Success -> {
                    pd.dismiss()
                    Log.e("AR_MYBUSS::", "UI Details: ${dataState.data}")
                    if(dataState.data.Success){
                        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
                        myEdit.putString("username", dataState.data.ResourceID)
                        myEdit.commit()
                        baseViewModel.updateLogin()
                        baseViewModel.updateUsername(dataState.data.ResourceID)
                        gotoLaunchpadPage(dataState.data.ResourceID)
                    }else{
                        Toast.makeText(this@LoginActivity,"Incorrect username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }.exhaustive
        }
    }

    private fun gotoLaunchpadPage(resourceId:String) {
        Intent(this@LoginActivity, DashboardActivity::class.java).apply {
            startActivity(this)
        }
    }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            if (snack.isShown) {
                this.toast(getString(R.string.text_network_connected))
            }
        } else {
            snack.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}