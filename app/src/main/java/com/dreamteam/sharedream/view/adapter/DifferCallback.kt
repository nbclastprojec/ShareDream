package com.dreamteam.sharedream.adapter

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.dreamteam.sharedream.model.PostData

object DifferCallback {
    val differCallback = object : DiffUtil.ItemCallback<PostData>(){
        override fun areItemsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem.image == newItem.image
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
    fun postClick(post: PostData)
}

interface ImgClick {
    fun imgClick(uri: Uri)
}