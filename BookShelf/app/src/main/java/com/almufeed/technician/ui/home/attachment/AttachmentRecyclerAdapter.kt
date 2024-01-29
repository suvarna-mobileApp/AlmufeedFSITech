package com.almufeed.technician.ui.home.attachment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almufeed.technician.business.domain.utils.dateFormater
import com.almufeed.technician.databinding.RecyclerAttachmentadapterBinding
import com.almufeed.technician.datasource.network.models.attachment.GetAttachmentData
import com.almufeed.technician.datasource.network.models.attachment.GetAttachmentResponseModel

class AttachmentRecyclerAdapter(val taskList: GetAttachmentResponseModel, val context: Context
) : RecyclerView.Adapter<AttachmentRecyclerAdapter.ItemViewHolder>() {
    /*private lateinit var decodedImage1 : Bitmap
    private lateinit var decodedImage2 : Bitmap
    private lateinit var decodedImage3 : Bitmap
    private lateinit var decodedImage4 : Bitmap
    private lateinit var decodedImage5 : Bitmap
    private lateinit var decodedImage6 : Bitmap
    private lateinit var decodedImage7 : Bitmap
    private lateinit var decodedImage8 : Bitmap
    private lateinit var decodedImage9 : Bitmap
    private lateinit var decodedImage10 : Bitmap
    private lateinit var decodedImage11 : Bitmap
    private lateinit var decodedImage12 : Bitmap
    private lateinit var decodedImage13 : Bitmap
    private lateinit var decodedImage14 : Bitmap
    private lateinit var decodedImage15 : Bitmap*/

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
      /*      if(currentItem.Image1?.isNotEmpty() == true){
                val decodedString1: ByteArray = Base64.decode(currentItem.Image1, Base64.DEFAULT)
                decodedImage1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.size)
                binding.captureImage1.setImageBitmap(decodedImage1)
            }

            if(currentItem.Image2?.isNotEmpty()== true){
                val decodedString2: ByteArray = Base64.decode(currentItem.Image2, Base64.DEFAULT)
                decodedImage2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.size)
                binding.captureImage2.setImageBitmap(decodedImage2)
            }

            if(currentItem.Image3?.isNotEmpty()== true){
                val decodedString3: ByteArray = Base64.decode(currentItem.Image3, Base64.DEFAULT)
                decodedImage3 = BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.size)
                binding.captureImage3.setImageBitmap(decodedImage3)
            }

            if(currentItem.Image4?.isNotEmpty()== true){
                val decodedString4: ByteArray = Base64.decode(currentItem.Image4, Base64.DEFAULT)
                decodedImage4 = BitmapFactory.decodeByteArray(decodedString4, 0, decodedString4.size)
                binding.linImage2.visibility = View.VISIBLE
                binding.captureImage4.setImageBitmap(decodedImage4)
            }

            if(currentItem.Image5?.isNotEmpty()== true){
                val decodedString5: ByteArray = Base64.decode(currentItem.Image5, Base64.DEFAULT)
                decodedImage5 = BitmapFactory.decodeByteArray(decodedString5, 0, decodedString5.size)
                binding.linImage2.visibility = View.VISIBLE
                binding.captureImage5.setImageBitmap(decodedImage5)
            }

            if(currentItem.Image6?.isNotEmpty()== true){
                val decodedString6: ByteArray = Base64.decode(currentItem.Image6, Base64.DEFAULT)
                decodedImage6 = BitmapFactory.decodeByteArray(decodedString6, 0, decodedString6.size)
                binding.linImage2.visibility = View.VISIBLE
                binding.captureImage6.setImageBitmap(decodedImage6)
            }

            if(currentItem.Image7?.isNotEmpty()== true){
                val decodedString7: ByteArray = Base64.decode(currentItem.Image7, Base64.DEFAULT)
                decodedImage7 = BitmapFactory.decodeByteArray(decodedString7, 0, decodedString7.size)
                binding.linImage3.visibility = View.VISIBLE
                binding.captureImage7.setImageBitmap(decodedImage7)
            }

            if(currentItem.Image8?.isNotEmpty()== true){
                val decodedString8: ByteArray = Base64.decode(currentItem.Image8, Base64.DEFAULT)
                decodedImage8 = BitmapFactory.decodeByteArray(decodedString8, 0, decodedString8.size)
                binding.linImage3.visibility = View.VISIBLE
                binding.captureImage8.setImageBitmap(decodedImage8)
            }

            if(currentItem.Image9?.isNotEmpty()== true){
                val decodedString9: ByteArray = Base64.decode(currentItem.Image9, Base64.DEFAULT)
                decodedImage9 = BitmapFactory.decodeByteArray(decodedString9, 0, decodedString9.size)
                binding.linImage3.visibility = View.VISIBLE
                binding.captureImage9.setImageBitmap(decodedImage9)
            }

            if(currentItem.Image10?.isNotEmpty()== true){
                val decodedString10: ByteArray = Base64.decode(currentItem.Image10, Base64.DEFAULT)
                decodedImage6 = BitmapFactory.decodeByteArray(decodedString10, 0, decodedString10.size)
                binding.linImage4.visibility = View.VISIBLE
                binding.captureImage10.setImageBitmap(decodedImage10)
            }

            if(currentItem.Image11?.isNotEmpty()== true){
                val decodedString11: ByteArray = Base64.decode(currentItem.Image11, Base64.DEFAULT)
                decodedImage11 = BitmapFactory.decodeByteArray(decodedString11, 0, decodedString11.size)
                binding.linImage4.visibility = View.VISIBLE
                binding.captureImage11.setImageBitmap(decodedImage11)
            }

            if(currentItem.Image12?.isNotEmpty()== true){
                val decodedString12: ByteArray = Base64.decode(currentItem.Image12, Base64.DEFAULT)
                decodedImage12 = BitmapFactory.decodeByteArray(decodedString12, 0, decodedString12.size)
                binding.linImage4.visibility = View.VISIBLE
                binding.captureImage12.setImageBitmap(decodedImage12)
            }

            if(currentItem.Image13?.isNotEmpty()== true){
                val decodedString13: ByteArray = Base64.decode(currentItem.Image13, Base64.DEFAULT)
                decodedImage13 = BitmapFactory.decodeByteArray(decodedString13, 0, decodedString13.size)
                binding.linImage5.visibility = View.VISIBLE
                binding.captureImage13.setImageBitmap(decodedImage13)
            }

            if(currentItem.Image14?.isNotEmpty()== true){
                val decodedString14: ByteArray = Base64.decode(currentItem.Image14, Base64.DEFAULT)
                decodedImage14 = BitmapFactory.decodeByteArray(decodedString14, 0, decodedString14.size)
                binding.linImage5.visibility = View.VISIBLE
                binding.captureImage14.setImageBitmap(decodedImage14)
            }

            if(currentItem.Image15?.isNotEmpty()== true){
                val decodedString15: ByteArray = Base64.decode(currentItem.Image15, Base64.DEFAULT)
                decodedImage15 = BitmapFactory.decodeByteArray(decodedString15, 0, decodedString15.size)
                binding.linImage5.visibility = View.VISIBLE
                binding.captureImage15.setImageBitmap(decodedImage15)
            }*/

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

               /* captureImage1.setImageBitmap(decodedImage1)
                captureImage2.setImageBitmap(decodedImage2)
                captureImage3.setImageBitmap(decodedImage3)*/
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