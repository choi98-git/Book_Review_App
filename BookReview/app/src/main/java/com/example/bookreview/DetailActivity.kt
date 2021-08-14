package com.example.bookreview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Database
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.bookreview.databinding.ActivityDetailBinding
import com.example.bookreview.model.Book
import com.example.bookreview.model.Review

class DetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB",
        ).build()

        val model = intent.getParcelableExtra<Book>("bookModel")

        binding.titleTextView.text = model?.title.orEmpty()
        binding.descriptionTextView.text = model?.description.orEmpty()

        Glide.with(binding.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        // 리뷰를 db에서 가져와 보여줌
        Thread{
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
            runOnUiThread {
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()

        // 작성된 리뷰 db에 저장
        binding.saveButton.setOnClickListener {
            Thread{
                db.reviewDao().saveReview(
                    Review(model?.id?.toInt() ?: 0,
                    binding.reviewEditText.text.toString()
                    )
                )
            }.start()
        }

    }
}