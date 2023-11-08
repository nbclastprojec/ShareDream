package com.dreamteam.sharedream.view.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.FragmentMyPageFavoritePostBinding
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.view.MyPageFavoritePost

class MyPageFavoritePostAdapter(private val postClick: PostClick) :
    ListAdapter<PostRcv, MyPageFavoritePostAdapter.MyPageFavoritePostViewHolder>(DifferCallback.differCallback) {

    inner class MyPageFavoritePostViewHolder(binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.writeTittle
        val subtitle = binding.writeSubtittle
        val value = binding.writePrice
        val category = binding.writeCategory
        val image = binding.writeImage

        fun bind(imagePath: Uri) {
            image.load(imagePath)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyPageFavoritePostViewHolder {
        return MyPageFavoritePostViewHolder(
            WriteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyPageFavoritePostViewHolder, position: Int) {
        val post = currentList[position]
        holder.itemView.setOnClickListener {
            postClick?.postClick(post)
        }

        val positionItem = currentList[position]

        holder.apply {
            category.text = "카테고리 : ${positionItem.category}"
            title.text = positionItem.title
            subtitle.text = positionItem.desc
            value.text = positionItem.price.toString()
            bind(positionItem.imgs.first())
            Log.d("xxxx", "onBindViewHolder: bind")
        }
    }
}