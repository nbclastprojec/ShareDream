package com.dreamteam.sharedream.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HomePostAdapter(private val postClick: PostClick) :
    ListAdapter<Post, HomePostAdapter.HomePostRcvViewHolder>(DifferCallback.differCallback) {

    private val storage = Firebase.storage

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostRcvViewHolder {
        return HomePostRcvViewHolder(
            WriteItemBinding.inflate(
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
        }

        holder.bind(positionItem.imgs[0])
    }

    inner class HomePostRcvViewHolder(binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postTitle: TextView = binding.writeTittle
        val postDesc: TextView = binding.writeSubtittle
        val postPrice: TextView = binding.writePrice
        val postCategory: TextView = binding.writeCategory
        val postImg: ImageView = binding.writeImage

        var imagePath: String? = null
        fun bind(imagePath : String) {

            if (this.imagePath != imagePath){
                //todo 글라이드 캐싱 추가 예정
//                storage.reference.child("post").child("$imagePath").downloadUrl.addOnSuccessListener { uri ->
//                 캐싱 - rcv 자체적인 캐시 or 페이징?
//                    Log.d("xxxx", "bind: storage download uri after img - $imagePath")
//                    Glide.with(itemView)
//                        .load(uri)
//                        .into(postImg)
//                    this.imagePath = imagePath
//                }
//                    .addOnFailureListener {
//                        Log.d("xxxx", " adapter bind Failure $it")
//                    }
//            } else {
//                Glide.with(itemView)
//                    .load(uri)
//                    .into(postImg)
//                this.imagePath = imagePath
//            }
            Log.d("xxxx", "bind: storage download uri before img - ${imagePath}")
            storage.reference.child("post").child("$imagePath").downloadUrl.addOnSuccessListener { uri ->
//                 캐싱 - rcv 자체적인 캐시 or 페이징?
                Log.d("xxxx", "bind: storage download uri after img - $imagePath")
                Glide.with(itemView)
                    .load(uri)
                    .into(postImg)
                this.imagePath = imagePath
            }
            .addOnFailureListener {
                Log.d("xxxx", " adapter bind Failure $it")
            }

            }
        }
    }
}