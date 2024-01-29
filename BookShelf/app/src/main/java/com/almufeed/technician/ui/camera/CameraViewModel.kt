package com.almufeed.technician.ui.camera

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val imageUri: MutableLiveData<Uri> =
        MutableLiveData()

    fun setImageUri(savedUri: Uri?) {
        imageUri.postValue(savedUri!!)
    }

    fun getImageUri(): Uri {
        return imageUri.value!!
    }

}
