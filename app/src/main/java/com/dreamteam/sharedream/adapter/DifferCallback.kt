package com.dreamteam.sharedream.adapter

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.dreamteam.sharedream.model.Post

object DifferCallback {
    val differCallback = object : DiffUtil.ItemCallback<Post>(){
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.deadline == newItem.deadline
        }
    }

    val uriDifferCallback = object : DiffUtil.ItemCallback<Uri>(){
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

    }
}

interface PostClick {
    fun postClick(post: Post)
}

interface ImgClick {
    fun imgClick(uri: Uri)
}