package com.almufeed.technician.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almufeed.technician.databinding.RecyclerFavoriteSingleRowBinding
import com.almufeed.technician.datasource.cache.models.book.BookEntity

class FavouriteRecyclerAdapter (val context: Context, val bookList: List<BookEntity>
) : RecyclerView.Adapter<FavouriteRecyclerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RecyclerFavoriteSingleRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = bookList.get(position)
        if (currentItem != null) {
            holder.bind(currentItem,position)
        }
    }

    inner class ItemViewHolder(private val binding: RecyclerFavoriteSingleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentItem: BookEntity, position: Int) {
            binding.apply {
               /* txtFavBookTitle.text = currentItem.name
                txtFavBookAuthor.text = currentItem.author
                txtDescBookPublishDate.text = "Expire On : "+ currentItem.publishdate
                txtDescBookPubliser.text = "Publisher : "+ currentItem.publisher
                btnRating.text = currentItem.rating
                Glide.with(binding.root.context)
                    .load(currentItem.image)
                    .into(binding.imgFavBookImage)*/
            }
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}