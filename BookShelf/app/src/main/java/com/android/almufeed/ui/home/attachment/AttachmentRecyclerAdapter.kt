package com.android.almufeed.ui.home.attachment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.almufeed.business.domain.utils.dateFormater
import com.android.almufeed.databinding.RecyclerAttachmentadapterBinding
import com.android.almufeed.datasource.network.models.attachment.GetAttachmentData
import com.android.almufeed.datasource.network.models.attachment.GetAttachmentResponseModel

class AttachmentRecyclerAdapter(val taskList: GetAttachmentResponseModel, val context: Context
) : RecyclerView.Adapter<AttachmentRecyclerAdapter.ItemViewHolder>() {
    private lateinit var decodedImage1 : Bitmap
    private lateinit var decodedImage2 : Bitmap
    private lateinit var decodedImage3 : Bitmap
    private lateinit var decodedImage4 : Bitmap
    private lateinit var decodedImage5 : Bitmap
    private lateinit var decodedImage6 : Bitmap

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RecyclerAttachmentadapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = taskList.fsiImage[position]
        if (currentItem != null) {
            holder.bind(currentItem,position)
        }
    }

    inner class ItemViewHolder(private val binding: RecyclerAttachmentadapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: GetAttachmentData, position: Int) {
            if(currentItem.Image1.isNotEmpty()){
                val decodedString1: ByteArray = Base64.decode(currentItem.Image1, Base64.DEFAULT)
                decodedImage1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.size)
                binding.captureImage1.setImageBitmap(decodedImage1)
            }

            if(currentItem.Image2.isNotEmpty()){
                val decodedString2: ByteArray = Base64.decode(currentItem.Image2, Base64.DEFAULT)
                decodedImage2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.size)
                binding.captureImage2.setImageBitmap(decodedImage1)
            }

            if(currentItem.Image3.isNotEmpty()){
                val decodedString3: ByteArray = Base64.decode(currentItem.Image3, Base64.DEFAULT)
                decodedImage3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.size)
                binding.captureImage3.setImageBitmap(decodedImage1)
            }

            if(currentItem.Image4.isNotEmpty()){
                val decodedString4: ByteArray = Base64.decode(currentItem.Image4, Base64.DEFAULT)
                decodedImage4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.size)
                binding.linImage2.visibility = View.VISIBLE
                binding.captureImage4.setImageBitmap(decodedImage1)
            }

            if(currentItem.Image5.isNotEmpty()){
                val decodedString5: ByteArray = Base64.decode(currentItem.Image5, Base64.DEFAULT)
                decodedImage5 = BitmapFactory.decodeByteArray(decodedString5, 0, decodedString5.size)
                binding.linImage2.visibility = View.VISIBLE
                binding.captureImage5.setImageBitmap(decodedImage1)
            }

            if(currentItem.Image6.isNotEmpty()){
                val decodedString6: ByteArray = Base64.decode(currentItem.Image6, Base64.DEFAULT)
                decodedImage6 = BitmapFactory.decodeByteArray(decodedString6, 0, decodedString6.size)
                binding.linImage2.visibility = View.VISIBLE
                binding.captureImage6.setImageBitmap(decodedImage1)
            }
           /* val decodedString1: ByteArray = Base64.decode(currentItem.Image1, Base64.DEFAULT)
            val decodedImage1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.size)

            val decodedString2: ByteArray = Base64.decode(currentItem.Image2, Base64.DEFAULT)
            val decodedImage2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.size)

            val decodedString3: ByteArray = Base64.decode(currentItem.Image3, Base64.DEFAULT)
            val decodedImage3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.size)
*/
            binding.apply {
               /* itemView.setOnClickListener {
                    val intent = Intent(context, CapturedPictureActivity::class.java)
                    intent.putExtra("taskid", currentItem.taskId)
                    intent.putExtra("image1", decodedString1)
                    intent.putExtra("image2", decodedString2)
                    intent.putExtra("image3", decodedString3)
                    intent.putExtra("type", currentItem.type)
                    intent.putExtra("date", currentItem.datetime)
                    intent.putExtra("des", currentItem.datetime)
                    context.startActivity(intent)
                }*/

                captureImage1.setImageBitmap(decodedImage1)
                captureImage2.setImageBitmap(decodedImage2)
                captureImage3.setImageBitmap(decodedImage3)
                dateTime.text = "Date :  " + dateFormater(currentItem.datetime)
                when(currentItem.type) {
                    0 -> txtImageType.text = "Image Type :  Before Task"
                    1 -> txtImageType.text = "Image Type :  After Task"
                    2 -> txtImageType.text = "Image Type :  Material Picture"
                    3 -> txtImageType.text = "Image Type :  Inspection"
                    else -> print("I don't know anything about it")
                }

                /*  btnOpen.setOnClickListener {
                      val intent = Intent(context, TaskDetailsActivity::class.java)
                      intent.putExtra("taskid", currentItem.id)
                      context.startActivity(intent)
                  }

                  btnAccept.setOnClickListener {
                      val intent = Intent(context, RiskAssessment::class.java)
                      intent.putExtra("taskid", currentItem.id)
                      context.startActivity(intent)
                  }*/
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.fsiImage.size
    }
}