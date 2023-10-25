package com.dreamteam.sharedream.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPostFeedAdapter(private val postClick: PostClick) :
    ListAdapter<Post, MyPostFeedAdapter.MyPostFeedRcvViewHolder>(DifferCallback.differCallback) {

    private val storage = Firebase.storage
    inner class MyPostFeedRcvViewHolder(binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.writeTittle
        val subtitle = binding.writeSubtittle
        val value = binding.writePrice
        val category = binding.writeCategory
        val image = binding.writeImage

        fun bind(imagePath : String) {
            storage.reference.child("post").child("$imagePath").downloadUrl.addOnSuccessListener { uri ->
                Glide.with(itemView)
                    .load(uri)
                    .into(image)
            }.addOnFailureListener {
                Log.d("xxxx", " adapter bind Failure $it")
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostFeedRcvViewHolder {
        return MyPostFeedRcvViewHolder(
            WriteItemBinding.inflate(
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
            category.text = "카테고리 : ${positionItem.category}"
            title.text = positionItem.title
            subtitle.text = positionItem.desc
            value.text = positionItem.price
            bind(positionItem.imgs.first())
            Log.d("xxxx", "onBindViewHolder: bind")
        }
    }
}