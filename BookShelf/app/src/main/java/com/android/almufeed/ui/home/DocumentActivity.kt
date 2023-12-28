package com.android.almufeed.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.almufeed.R
import com.android.almufeed.databinding.ActivityDocumentBinding

class DocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false);
        }

        binding.apply {
           /* toolbar.setText("Documents")
            incToolbarImage.visibility = View.VISIBLE

            incToolbarImage.setOnClickListener (View.OnClickListener { view ->
                this@DocumentActivity.onBackPressedDispatcher.onBackPressed()
            })*/

            txtDoc1.setOnClickListener {
                val intent = Intent(this@DocumentActivity, PDFVIew::class.java)
                intent.putExtra("url","workplace.pdf")
                startActivity(intent)
            }

            txtDoc2.setOnClickListener {
                val intent = Intent(this@DocumentActivity, PDFVIew::class.java)
                intent.putExtra("url","customerServicePPM.pdf")
                startActivity(intent)
            }

            txtDoc3.setOnClickListener {
                val intent = Intent(this@DocumentActivity, PDFVIew::class.java)
                intent.putExtra("url","training_PPM.pdf")
                startActivity(intent)
            }

            txtDoc4.setOnClickListener {
                val intent = Intent(this@DocumentActivity, PDFVIew::class.java)
                intent.putExtra("url","technicianMobileTraining.pdf")
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}