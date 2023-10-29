package com.dreamteam.sharedream.adapter

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv

object DifferCallback {
    val differCallback = object : DiffUtil.ItemCallback<PostRcv>(){
        override fun areItemsTheSame(oldItem: PostRcv, newItem: PostRcv): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PostRcv, newItem: PostRcv): Boolean {
            return oldItem == newItem
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
    fun postClick(post: PostRcv)
}

interface ImgClick {
    fun imgClick(uri: Uri)
}