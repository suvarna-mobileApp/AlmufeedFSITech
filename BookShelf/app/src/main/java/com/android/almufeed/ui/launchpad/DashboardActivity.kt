package com.android.almufeed.ui.launchpad

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.almufeed.R
import com.android.almufeed.business.domain.utils.toast
import com.android.almufeed.databinding.ActivityDashboardBinding
import com.android.almufeed.ui.base.BaseInterface
import com.android.almufeed.ui.base.BaseViewModel
import com.android.almufeed.ui.home.DocumentActivity
import com.android.almufeed.ui.home.TaskActivity
import com.android.almufeed.ui.services.AlarmReceiver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() , BaseInterface {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var snack: Snackbar
    private val baseViewModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
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

        binding.apply {

            txtDoc.setOnClickListener {
                Intent(this@DashboardActivity, DocumentActivity::class.java).apply {
                    startActivity(this)
                }
            }

            txtActivity.setOnClickListener {
                Intent(this@DashboardActivity, TaskActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        /*val alarm = AlarmReceiver()
        alarm.setAlarm(this)*/
    }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            if (snack.isShown) {
                this.toast(getString(com.android.almufeed.R.string.text_network_connected))
            }
        } else {
            snack.show()
        }
    }
}