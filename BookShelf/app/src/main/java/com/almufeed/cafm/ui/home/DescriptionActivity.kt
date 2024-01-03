package com.almufeed.cafm.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import com.almufeed.cafm.R
import com.almufeed.cafm.databinding.ActivityDescriptionBinding
import com.almufeed.cafm.datasource.network.models.bookList.BookData

class DescriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDescriptionBinding
    private lateinit var bookDetails: BookData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = "${resources.getString(R.string.detail)}"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_actionbar_backbutton)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        if (intent != null) {
            bookDetails = intent.getParcelableExtra("book_details")!!
        } else {
            finish()
            Toast.makeText(this@DescriptionActivity, "Some error occurred!", Toast.LENGTH_SHORT)
                .show()
        }
       /* binding.apply {
            Glide.with(binding.root.context)
                .load(bookDetails.volumeInfo.imageLinks?.thumbnail)
                .into(binding.imgDescBookImage)
            txtDescBookName.text = bookDetails.volumeInfo.title
            txtDescBookAuthor.text = "Author : ${bookDetails.volumeInfo.authors?.get(0)}"
            btnRating.text = bookDetails.volumeInfo.averageRating.toString()
            txtDescBookPublishDate.text = "Published date : "+ bookDetails.volumeInfo.publishedDate
            txtDescBookPubliser.text = "Publisher : "+ bookDetails.volumeInfo.publisher
            txtDescBookPubliser.text = bookDetails.volumeInfo.publisher
            txtDescBookDescription.text =bookDetails.volumeInfo.description
            txtDescBookPublishDate.text =bookDetails.volumeInfo.publishedDate

            val bookEntity = BookEntity(
                bookDetails.volumeInfo.pageCount,
                bookDetails.id,
                txtDescBookName.text.toString(),
                txtDescBookAuthor.text.toString(),
                txtDescBookDescription.text.toString(),
                txtDescBookPubliser.text.toString(),
                txtDescBookPublishDate.text.toString(),
                bookDetails.volumeInfo.averageRating.toString(),
                bookDetails.volumeInfo.imageLinks?.thumbnail.toString()
            )

            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
            val isFave = checkFav.get()
            if (isFave) {
                btnAddtoFav.text = "Remove from favourites"
                val favcolor = ContextCompat.getColor(
                    applicationContext,
                    R.color.primary
                )
                btnAddtoFav.setBackgroundColor(favcolor)
            } else {
                btnAddtoFav.text = resources.getString(R.string.add_to_favourites)
                val nofavcolor = ContextCompat.getColor(
                    applicationContext,
                    R.color.primary
                )
                btnAddtoFav.setBackgroundColor(nofavcolor)
            }
            btnAddtoFav.setOnClickListener {
                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                        .get()
                ) {
                    val async =
                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                    val result = async.get()
                    if (result) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            resources.getString(R.string.add),
                            Toast.LENGTH_SHORT
                        ).show()

                        btnAddtoFav.text = resources.getString(R.string.remove_to_favourites)
                        val newcolor = ContextCompat.getColor(
                            applicationContext,
                            R.color.primary
                        )
                        btnAddtoFav.setBackgroundColor(newcolor)
                    } else {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val async =
                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                    val result = async.get()
                    if (result) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            resources.getString(R.string.remove),
                            Toast.LENGTH_SHORT
                        ).show()
                        btnAddtoFav.text = resources.getString(R.string.add_to_favourites)
                        val newcolor = ContextCompat.getColor(
                            applicationContext,
                            R.color.primary
                        )
                        btnAddtoFav.setBackgroundColor(newcolor)
                    }
                }
            }
        }*/
    }


  /*  class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        *//*
        1-> Check if in favs or not
        2-> Save in favs
        3->Remove from favs
         *//*

        //building database using the database class
        val db = Room.databaseBuilder(context, BookDatabase::class.java, DATABASE_NAME).build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.task_id)
                    db.close()
                    return book != null
                }
                //check fav
                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                    //as no chance to get a null value
                }
                //add to fav
                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                    //remove from fav
                }
            }

            //for the time being
            return false
        }
    }
*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}