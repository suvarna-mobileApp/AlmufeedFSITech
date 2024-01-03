package com.almufeed.cafm.business.domain.utils

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.*
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


val <T>T.exhaustive: T
    get() = this

fun <T> AppCompatActivity.collectLatestFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            //It will check for both wifi and cellular network
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    } else {
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateOnly(date: String): String {
    val dateTime: ZonedDateTime = OffsetDateTime.parse(date).toZonedDateTime()
    val defaultZoneTime: ZonedDateTime = dateTime.withZoneSameInstant(ZoneId.systemDefault())
    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    var d: Date? = null
    try {
        d = input.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    //val formatted: String = output.format(d)

    val formatted: String = defaultZoneTime.format(formatter)
    Log.e("DATE::", "$formatted")
    return formatted
}

fun getDate(dateStr: String?) {
    try {
        /** DEBUG dateStr = '2006-04-16T04:00:00Z' **/
        val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH)
        val mDate = formatter.parse(dateStr) // this never ends while debugging
        Log.e("mDate", mDate.toString())
    } catch (e: Exception){
        Log.e("mDate",e.toString()) // this never gets called either
    }
}

fun dateFormater(oldFormat: String?): String? {
    var convertedDate: String? = null
    try {
        val originalFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.ENGLISH)
        val targetFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy  hh:mm a", Locale.ENGLISH)
        val date: Date = originalFormat.parse(oldFormat)
        convertedDate = targetFormat.format(date)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return convertedDate
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatQcDate(date: String?): String {
    val dateTime: ZonedDateTime = OffsetDateTime.parse(date).toZonedDateTime()
    val defaultZoneTime: ZonedDateTime = dateTime.withZoneSameInstant(ZoneId.systemDefault())
    val input = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
    /*val output = SimpleDateFormat("dd-MM-yyyy hh:mm a")
    output.timeZone = TimeZone.getTimeZone("GMT+5:30")*/

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")

    var d: Date? = null
    try {
        d = input.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    //val formatted: String = output.format(d)

    val formatted: String = defaultZoneTime.format(formatter)

    Log.e("DATE::", "$formatted")
    return formatted
}


fun <T> Fragment.collectLatestFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}
fun compressImage(imageUri: String,context: Context): String? {
    val filePath = getRealPathFromURI(imageUri,context)
    var scaledBitmap: Bitmap? = null
    val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
    options.inJustDecodeBounds = true
    var bmp = BitmapFactory.decodeFile(filePath, options)
    var actualHeight = options.outHeight
    var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
    val maxHeight = 816.0f
    val maxWidth = 612.0f
    var imgRatio = (actualWidth / actualHeight).toFloat()
    val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        if (imgRatio < maxRatio) {
            imgRatio = maxHeight / actualHeight
            actualWidth = (imgRatio * actualWidth).toInt()
            actualHeight = maxHeight.toInt()
        } else if (imgRatio > maxRatio) {
            imgRatio = maxWidth / actualWidth
            actualHeight = (imgRatio * actualHeight).toInt()
            actualWidth = maxWidth.toInt()
        } else {
            actualHeight = maxHeight.toInt()
            actualWidth = maxWidth.toInt()
        }
    }

//      setting inSampleSize value allows to load a scaled down version of the original image
    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
    options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
    options.inPurgeable = true
    options.inInputShareable = true
    options.inTempStorage = ByteArray(16 * 1024)
    try {
//          load the bitmap from its path
        bmp = BitmapFactory.decodeFile(filePath, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    val canvas = Canvas(scaledBitmap!!)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp,
        middleX - bmp.width / 2,
        middleY - bmp.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )

//      check the rotation of the image and display it properly
    val exif: ExifInterface
    try {
        exif = ExifInterface(filePath.toString())
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 0
        )
        Log.d("EXIF", "Exif: $orientation")
        val matrix = Matrix()
        if (orientation == 6) {
            matrix.postRotate(90F)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 3) {
            matrix.postRotate(180F)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 8) {
            matrix.postRotate(270F)
            Log.d("EXIF", "Exif: $orientation")
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap!!, 0, 0,
            scaledBitmap.width, scaledBitmap.height, matrix,
            true
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }
    var out: FileOutputStream? = null
    val filename = getFilename()
    try {
        out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return filename
}

fun getFilename(): String {
    val file = File(Environment.getExternalStorageDirectory().path, "MyFolder/Images")
    if (!file.exists()) {
        file.mkdirs()
    }
    return file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg"
}

private fun getRealPathFromURI(contentURI: String,context: Context): String? {
    val contentUri = Uri.parse(contentURI)
    val cursor: Cursor = context.getContentResolver().query(contentUri, null, null, null, null)!!
    return if (cursor == null) {
        contentUri.path
    } else {
        cursor.moveToFirst()
        val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        cursor.getString(index)
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    return inSampleSize
}
fun Context.toast(message: String?) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun <T> AppCompatActivity.collectLatestFlow2(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        flow.collectLatest(collect)
    }
}

fun TextInputLayout.errorListener(editText: TextInputEditText) {
    val inputLayout = this
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            //Log.e("ER::", "afterTextChanged : 1")
        }

        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
            //Log.e("ER::", "beforeTextChanged : 2")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.e("ER::", "onTextChanged : 3")
            inputLayout.isErrorEnabled = false
        }
    })
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}