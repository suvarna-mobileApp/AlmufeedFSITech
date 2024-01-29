package com.almufeed.technician.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.almufeed.technician.databinding.ActivitySignaturePadBinding

class SignaturePadActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignaturePadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignaturePadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener { view ->
            val bitmap: Bitmap = binding.signatureView.getSignatureBitmap()
            if (bitmap != null) {
               // binding.imgSignature.setImageBitmap(bitmap)
            }
        }

        binding.btnClear.setOnClickListener { view -> binding.signatureView.clearCanvas() }
    }
}