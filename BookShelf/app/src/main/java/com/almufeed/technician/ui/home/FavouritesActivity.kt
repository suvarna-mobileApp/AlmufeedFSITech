package com.almufeed.technician.ui.home

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.almufeed.technician.R
import com.almufeed.technician.business.domain.utils.exceptionView.ExceptionItems
import com.almufeed.technician.business.domain.utils.exceptionView.createExceptionView
import com.almufeed.technician.databinding.ActivityFavouritesBinding
import com.almufeed.technician.datasource.cache.database.BookDatabase
import com.almufeed.technician.datasource.cache.models.book.BookEntity
import com.almufeed.technician.ui.home.adapter.FavouriteRecyclerAdapter

class FavouritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouritesBinding
    private lateinit var favouriteRecyclerAdapter : FavouriteRecyclerAdapter

    //for book data
    var bookList = listOf<BookEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "${resources.getString(R.string.menu_description)}"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        binding.apply {
            displayProgressBar(false)

            emptyView.visibility = View.GONE
            createExceptionView(
                ExceptionItems.EMPTY.label,
                layoutInflater,
                emptyView,
                ""
            )

            bookList = RetrieveFavourites(this@FavouritesActivity).execute().get()
            if(bookList.isEmpty()){
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }
            binding.recyclerFavourites.apply {
                favouriteRecyclerAdapter = FavouriteRecyclerAdapter(this@FavouritesActivity, bookList)
                layoutManager = GridLayoutManager(this@FavouritesActivity,2)
                recyclerFavourites.adapter = favouriteRecyclerAdapter
            }
        }
    }

    private fun displayProgressBar(isDisplayed: Boolean) {
        binding.progressBar.visibility = if (isDisplayed) View.VISIBLE else View.INVISIBLE
    }
    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<BookEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "Book_DB").build()

            return db.bookDao().getAllBooks()
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