package com.android.almufeed.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.almufeed.databinding.RecyclerFavoriteSingleRowBinding
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_favorite_single_row.view.*

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