package com.android.almufeed.ui.camera

import android.content.Context
import android.os.Bundle
import com.android.almufeed.R
import com.android.almufeed.databinding.ActivityCameraBinding
import com.android.almufeed.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CameraActivity : BaseActivity() {
    companion object {
        const val IMAGE_URI: String = "ImageUri"
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, "Assesmentbook").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

    private lateinit var viewBinding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {

    }
}
