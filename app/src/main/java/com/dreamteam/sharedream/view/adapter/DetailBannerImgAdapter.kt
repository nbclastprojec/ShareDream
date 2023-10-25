package com.dreamteam.sharedream.view.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.VpItemImgBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class DetailBannerImgAdapter(private val uris : List<String>) :
    RecyclerView.Adapter<DetailBannerImgAdapter.EventViewHolder>() {

    private val storage = Firebase.storage
    inner class EventViewHolder(binding: VpItemImgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val bannerImgUrl: ImageView = binding.detailImg

        fun bind(imagePath : String){
            storage.reference.child("post").child("${imagePath}")
                .downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(itemView)
                        .load(uri)
                        .into(bannerImgUrl)
                    Log.d("xxxx", "bind: Successful : $uri")
                }.addOnFailureListener {
                    Log.d("xxxx", "bind Failure: $it ")
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            VpItemImgBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return uris.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(uris[position])
    }
}
