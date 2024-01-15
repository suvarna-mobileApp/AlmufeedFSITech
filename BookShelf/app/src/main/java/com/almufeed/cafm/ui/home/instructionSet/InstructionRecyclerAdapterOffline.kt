package com.almufeed.cafm.ui.home.instructionSet

import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almufeed.cafm.R
import com.almufeed.cafm.databinding.RecyclerInstructionadapterBinding
import com.almufeed.cafm.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.cafm.datasource.network.models.instructionSet.InstructionData
import com.almufeed.cafm.datasource.network.models.instructionSet.InstructionSetResponseModel

class InstructionRecyclerAdapterOffline (val dbinstructionList: List<GetInstructionSetEntity>,
                                         val context: Context,
                                         val listener: OnItemClickListener
) : RecyclerView.Adapter<InstructionRecyclerAdapterOffline.ItemViewHolder>() {
    private val DELAY_MS: Long = 1000
    private val handler = Handler()
    private val selectedItems = mutableSetOf<Int>() // Use a Set to store selected item positions
    private val selectedItemsNo = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RecyclerInstructionadapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = dbinstructionList.get(position)
        holder.bind(currentItem,position)
    }

    inner class ItemViewHolder(val binding: RecyclerInstructionadapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: GetInstructionSetEntity, position: Int) {
            binding.apply {

                /*feedbacktype = 0 = nothing
                feedbacktype = 1 = yesno
                feedbacktype = 2 = heading
                feedbacktype = 3 = freetext*/

                txtProblem.text = currentItem.LineNumber.toString() + " . " + currentItem.Steps
                when(currentItem.FeedbackType) {
                    0 -> {
                        binding.checklist.linYesno.visibility = View.GONE
                        binding.etMessage.visibility = View.GONE
                        listener.onItemClick(currentItem.Refrecid, currentItem.FeedbackType,
                            currentItem.Steps.toString(),position
                        )
                    }

                    1 -> {
                        binding.checklist.linYesno.visibility = View.VISIBLE
                        binding.etMessage.visibility = View.GONE
                    }

                    2 -> {
                        binding.checklist.linYesno.visibility = View.GONE
                        binding.etMessage.visibility = View.GONE
                    }

                    3 -> {
                        binding.checklist.linYesno.visibility = View.GONE
                        binding.etMessage.visibility = View.VISIBLE
                    }
                    else -> print("I don't know anything about it")
                }

                /* binding.etMessage.setOnFocusChangeListener(object : OnFocusChangeListener {
                     override fun onFocusChange(view: View, hasFocus: Boolean) {
                         if (hasFocus) {
                             Toast.makeText(context, "Typing", Toast.LENGTH_SHORT).show()
                         } else {
                             listener.onItemClick(currentItem.Refrecid, currentItem.FeedbackType,binding.etMessage.text.toString())
                             Toast.makeText(context, "Typing Stop", Toast.LENGTH_SHORT).show()
                         }
                     }
                 })*/

                /*  binding.checklist.btnNo.setTextColor(context.getColor(R.color.black))
                  binding.checklist.btnNo.setBackgroundColor(context.getColor(R.color.white))
                  binding.checklist.btnYes.setTextColor(context.getColor(R.color.black))
                  binding.checklist.btnYes.setBackgroundColor(context.getColor(R.color.white))*/

                binding.etMessage.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        val fullString = s.toString()
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed({
                            CheckListActivity.clickedButtonCount++
                            listener.onItemClick(currentItem.Refrecid, currentItem.FeedbackType,fullString,position)
                        }, DELAY_MS)
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int,
                                                   count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence, start: Int,
                                               before: Int, count: Int) {

                    }
                })

                binding.checklist.btnYes.setOnClickListener {
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(currentItem.Refrecid, currentItem.FeedbackType,"Yes",position)
                        if (selectedItems.contains(position)) {
                            // Item was already selected, unselect it
                            //System.out.println("suvarna yes ")
                            selectedItems.remove(position)
                        } else {
                            // Item was not selected, select it
                            selectedItems.add(position)
                            CheckListActivity.clickedButtonCount++
                            if (selectedItemsNo.contains(position)) {
                                CheckListActivity.clickedButtonCount--
                                selectedItemsNo.remove(position)
                            }
                        }
                        notifyDataSetChanged()
                    }
                }

                if (selectedItems.contains(position)) {
                    // Item is selected, set button color to blue
                    binding.checklist.btnYes.setTextColor(context.getColor(R.color.white))
                    binding.checklist.btnYes.setBackgroundColor(context.getColor(R.color.primary))
                    binding.checklist.btnNo.setTextColor(context.getColor(R.color.black))
                    binding.checklist.btnNo.setBackgroundColor(context.getColor(R.color.white))
                } else if(selectedItemsNo.contains(position)){
                    // Item is not selected, set button color to the default color
                    binding.checklist.btnYes.setTextColor(context.getColor(R.color.black))
                    binding.checklist.btnYes.setBackgroundColor(context.getColor(R.color.white))
                    binding.checklist.btnNo.setTextColor(context.getColor(R.color.white))
                    binding.checklist.btnNo.setBackgroundColor(context.getColor(R.color.primary))
                }else{
                    binding.checklist.btnYes.setTextColor(context.getColor(R.color.black))
                    binding.checklist.btnYes.setBackgroundColor(context.getColor(R.color.white))
                    binding.checklist.btnNo.setTextColor(context.getColor(R.color.black))
                    binding.checklist.btnNo.setBackgroundColor(context.getColor(R.color.white))
                }

                binding.checklist.btnNo.setOnClickListener {
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(currentItem.Refrecid, currentItem.FeedbackType,"No",position)
                        if (selectedItemsNo.contains(position)) {
                            // Item was already selected, unselect it
                            selectedItemsNo.remove(position)
                        } else {
                            // Item was not selected, select it
                            selectedItemsNo.add(position)
                            CheckListActivity.clickedButtonCount++
                            if (selectedItems.contains(position)) {
                                CheckListActivity.clickedButtonCount--
                                selectedItems.remove(position)
                            }
                        }
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dbinstructionList.size
    }

    interface OnItemClickListener {
        fun onItemClick(refId: Long, feedBackType: Int, answer: String,position:Int)
    }
}