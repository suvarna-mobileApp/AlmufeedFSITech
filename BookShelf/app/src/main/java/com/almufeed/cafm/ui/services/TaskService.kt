package com.almufeed.cafm.ui.services

import android.app.Service
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.almufeed.cafm.business.domain.state.DataState
import com.almufeed.cafm.business.domain.utils.exhaustive
import com.almufeed.cafm.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskService : Service(), ViewModelStoreOwner, LifecycleOwner {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val thread = HandlerThread("ServiceStartArguments", 1)
        thread.start()
        Log.d("onCreate()", "After service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        homeViewModel.requestForTask()
        subscribeObservers()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun subscribeObservers() {
        homeViewModel.myTaskDataSTate.observe(this) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    dataState.exception.message
                }
                is DataState.Loading -> {

                }
                is DataState.Success -> {
                    Log.e("AR_MYBUSS::", "Task Details: ${dataState.data}")
                    Toast.makeText(this, "Freshly Made toast!", Toast.LENGTH_SHORT).show()
                    }
                }
            }.exhaustive
        }

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }
}