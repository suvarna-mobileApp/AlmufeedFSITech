package com.almufeed.cafm.ui.launchpad

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.almufeed.cafm.R
import com.almufeed.cafm.business.domain.utils.dataStore.BasePreferencesManager
import com.almufeed.cafm.business.domain.utils.isOnline
import com.almufeed.cafm.business.domain.utils.toast
import com.almufeed.cafm.databinding.ActivityDashboardBinding
import com.almufeed.cafm.datasource.cache.database.BookDatabase
import com.almufeed.cafm.ui.base.BaseInterface
import com.almufeed.cafm.ui.base.BaseViewModel
import com.almufeed.cafm.ui.home.DocumentActivity
import com.almufeed.cafm.ui.home.SyncWithServer
import com.almufeed.cafm.ui.home.TaskActivity
import com.almufeed.cafm.ui.login.LoginActivity
import com.almufeed.cafm.ui.services.AlarmReceiver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() , BaseInterface {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var snack: Snackbar
    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var db : BookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Room.databaseBuilder(this@DashboardActivity, BookDatabase::class.java, BookDatabase.DATABASE_NAME).allowMainThreadQueries().build()

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


            lifecycleScope.launch {
                val userName = baseViewModel.getUsername()
                txtUsername.setText("USER NAME - " + userName + " (Version 1)")
            }

            txtLogout.setOnClickListener {
                baseViewModel.setToken("")
                baseViewModel.updateUsername("")
                Intent(this@DashboardActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                }
            }

            txtActivity.setOnClickListener {
                Intent(this@DashboardActivity, TaskActivity::class.java).apply {
                    startActivity(this)
                }
            }

            txtSync.setOnClickListener {
                if(isOnline(this@DashboardActivity)){
                    val customerDetail = db.bookDao().getAllCustomerDetails()
                    val attachmentList = db.bookDao().getAllAttachmentSet()
                    val instructionList = db.bookDao().getAllInstructionSet()
                    val ratingList = db.bookDao().getAllRating()
                    val eventList = db.bookDao().getAllEvents()
                    if(customerDetail.size > 0 || attachmentList.size > 0 || instructionList.size > 0 || ratingList.size > 0 || eventList.size > 0){
                        Intent(this@DashboardActivity, SyncWithServer::class.java).apply {
                            startActivity(this)
                        }
                    }else{
                        Toast.makeText(this@DashboardActivity,"No Data to sync",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@DashboardActivity,"No Internet Connection",Toast.LENGTH_SHORT).show()
                }
            }
        }

        val alarm = com.almufeed.cafm.ui.services.AlarmReceiver()
        alarm.setAlarm(this)
    }

    suspend fun userName(){

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            if (snack.isShown) {
                this.toast(getString(com.almufeed.cafm.R.string.text_network_connected))
            }
        } else {
            snack.show()
        }
    }
}