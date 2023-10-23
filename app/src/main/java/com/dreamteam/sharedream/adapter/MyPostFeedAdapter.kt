package com.dreamteam.sharedream.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.RcvItemPostBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPostFeedAdapter(private val postClick: PostClick) :
    ListAdapter<Post, MyPostFeedAdapter.MyPostFeedRcvViewHolder>(DifferCallback.differCallback) {

    private val storage = Firebase.storage
    inner class MyPostFeedRcvViewHolder(binding: RcvItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postTitle: TextView = binding.postTitle
        val postDesc: TextView = binding.postDesc
        val postPrice: TextView = binding.postPrice
        val postCategory: TextView = binding.postCategory
        val postImg: ImageView = binding.postImg

        fun bind(imagePath : String) {
            storage.reference.child("post").child("$imagePath").downloadUrl.addOnSuccessListener { uri ->
                Glide.with(itemView)
                    .load(uri)
                    .into(postImg)
            }.addOnFailureListener {
                Log.d("xxxx", " adapter bind Failure $it")
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostFeedRcvViewHolder {
        return MyPostFeedRcvViewHolder(
            RcvItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyPostFeedRcvViewHolder, position: Int) {
        val post = currentList[position]
        holder.itemView.setOnClickListener {
            postClick?.postClick(post)
        }

        val positionItem = currentList[position]

        holder.apply {
            postCategory.text = "카테고리 : ${positionItem.category}"
            postTitle.text = positionItem.title
            postDesc.text = positionItem.desc
            postPrice.text = positionItem.price
            bind(positionItem.imgs.first())
        }
    }
}