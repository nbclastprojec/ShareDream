package com.dreamteam.sharedream.adapter

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import com.dreamteam.sharedream.model.AlarmPost
import com.dreamteam.sharedream.model.Post

object DifferCallback {
    val differCallback = object : DiffUtil.ItemCallback<AlarmPost>(){
        override fun areItemsTheSame(oldItem: AlarmPost, newItem: AlarmPost): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AlarmPost, newItem: AlarmPost): Boolean {
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
    fun postClick(post: AlarmPost)
}

interface ImgClick {
    fun imgClick(uri: Uri)
}