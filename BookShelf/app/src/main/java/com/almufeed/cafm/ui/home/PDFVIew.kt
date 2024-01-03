package com.almufeed.cafm.ui.home

import android.app.ProgressDialog
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.almufeed.cafm.R
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL

class PDFVIew : AppCompatActivity() {
    lateinit var backPress: ImageView
    lateinit var toolbar: Toolbar
    lateinit var pdfView : PDFView
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)
        pdfView = findViewById<PDFView>(R.id.pdfView) as PDFView
        toolbar = findViewById<Toolbar>(R.id.toolbar)

        val intent = getIntent()
        var pdfUrl = intent.getStringExtra("url").toString()

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false);
        }

       /* backPress.setOnClickListener (View.OnClickListener { view ->
            this@PDFVIew.onBackPressedDispatcher.onBackPressed()
        })*/

        pdfView.fromAsset(pdfUrl)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .defaultPage(0)
            .enableAnnotationRendering(false)
            .password(null)
            .scrollHandle(null)
            .enableAntialiasing(true)
            .spacing(4)
            .invalidPageColor(Color.WHITE)
            .load()

       /* progressDialog = ProgressDialog(this@PDFVIew)
        progressDialog.setMessage("Loading PDF file....")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()*/
        //MyAsyncTask(this@PDFVIew,viewUrl).execute()
    }

    class MyAsyncTask internal constructor(context: PDFVIew,viewPdfUrl: String) : AsyncTask<Unit, Unit, InputStream>() {
        private val activityReference: WeakReference<PDFVIew> = WeakReference(context)
        var viewUrl = viewPdfUrl
        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: Unit): InputStream {
            return URL(viewUrl).openStream()
        }

        override fun onPostExecute(result: InputStream) {
            val activity = activityReference.get()
            activity!!.pdfView.fromStream(result)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(4)
                .invalidPageColor(Color.WHITE)
                .onLoad(object : OnLoadCompleteListener {
                    override fun loadComplete(nbPages: Int) {
                        activity.progressDialog.dismiss()
                    }
                })
                .load()
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