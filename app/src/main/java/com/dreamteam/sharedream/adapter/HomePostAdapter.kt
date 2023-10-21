package com.dreamteam.sharedream.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.databinding.RcvItemPostBinding
import com.dreamteam.sharedream.model.Post

class HomePostAdapter(private val postClick: PostClick) :
    ListAdapter<Post, HomePostAdapter.HomePostRcvViewHolder>(DifferCallback.differCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostRcvViewHolder {
        return HomePostRcvViewHolder(
            RcvItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomePostRcvViewHolder, position: Int) {
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
            postImg.load(positionItem.img)
        }
    }

    inner class HomePostRcvViewHolder(binding: RcvItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postTitle: TextView = binding.postTitle
        val postDesc: TextView = binding.postDesc
        val postPrice: TextView = binding.postPrice
        val postCategory: TextView = binding.postCategory
        val postImg: ImageView = binding.postImg
    }
}