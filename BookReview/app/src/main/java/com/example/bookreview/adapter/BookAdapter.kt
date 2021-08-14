package com.example.bookreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookreview.databinding.ItemBookBinding
import com.example.bookreview.model.Book

class BookAdapter(private val itemClickedListener: (Book) -> Unit):ListAdapter<Book, BookAdapter.BookItemViewHolder>(diffUtill) {

    inner class BookItemViewHolder(private val binding: ItemBookBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(bookModel: Book){
            binding.titleTextView.text = bookModel.title
            binding.descriptionTextView.text = bookModel.description

            binding.root.setOnClickListener {
                itemClickedListener(bookModel)
            }

            Glide
                .with(binding.coverImageView.context)
                .load(bookModel.coverSmallUrl)
                .into(binding.coverImageView)
        }
    }

    // 미리 만든 뷰 홀더가 없을 경우 사용
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        return BookItemViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // 뷰 홀더가 뷰에 그려질 때 데이터를 바인드해서 그려줌
    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        // 똑같은 아이템을 그리지 않기위함
        val diffUtill = object : DiffUtil.ItemCallback<Book>(){

            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
               return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}