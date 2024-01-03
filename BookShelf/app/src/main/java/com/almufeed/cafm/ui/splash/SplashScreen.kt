package com.almufeed.cafm.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.almufeed.cafm.R
import com.almufeed.cafm.ui.splash.SplashScreenViewModel
import com.almufeed.cafm.business.domain.utils.collectLatestFlow
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.business.domain.utils.toast
import com.almufeed.cafm.databinding.ActivitySplashScreenBinding
import com.almufeed.cafm.ui.base.BaseInterface
import com.almufeed.cafm.ui.base.BaseViewModel
import com.almufeed.cafm.ui.launchpad.DashboardActivity
import com.almufeed.cafm.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreen : AppCompatActivity(), BaseInterface {
    val TIME_OUT : Long = 2000
    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var snack: Snackbar
    private val REQUEST_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseViewModel.isNetworkConnected.observe(this) { isNetworkAvailable ->
            showNetworkSnackBar(isNetworkAvailable)
        }

        binding.apply {
            snack = Snackbar.make(
                root,
                getString(R.string.text_network_not_available),
                Snackbar.LENGTH_INDEFINITE
            )
        }

        binding.imgSplash.postDelayed({
            subscribeObservers()
            splashScreenViewModel.initViewModel()
        }, TIME_OUT)
    }

    private fun subscribeObservers() {
        collectLatestFlow(splashScreenViewModel.splashChannelFlow) { event ->
            when (event) {
                SplashScreenViewModel.SplashEvent.NavigateToLoginActivity -> {
                    gotoLoginPage()
                }
                SplashScreenViewModel.SplashEvent.NavigateToLaunchpadActivity -> {
                    gotoLaunchpadPage()
                }
                else -> {}
            }.exhaustive
        }
    }
    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this,
                 arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                 REQUEST_PERMISSION)
        }else{
            subscribeObservers()
            splashScreenViewModel.initViewModel()
        }
    }

    private fun gotoLoginPage() {
        Intent(this@SplashScreen, LoginActivity::class.java).apply {
            startActivity(this)
        }
        finish()
    }

    private fun gotoLaunchpadPage() {
         Intent(this@SplashScreen, DashboardActivity::class.java).apply {
             startActivity(this)
         }
         finish()
    }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            if (snack.isShown) {
                this.toast(getString(R.string.text_network_connected))
                snack.dismiss()
            }
        } else {
            snack.show()
        }
    }
}