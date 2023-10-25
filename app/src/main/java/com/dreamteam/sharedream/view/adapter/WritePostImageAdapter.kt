package com.dreamteam.sharedream.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.ImgClick
import com.dreamteam.sharedream.databinding.EditImageItemBinding
import com.dreamteam.sharedream.databinding.RcvItemWriteImgBinding
import com.dreamteam.sharedream.databinding.WriteItemBinding

class WritePostImageAdapter(private val itemClick: ImgClick) :
    ListAdapter<Uri, WritePostImageAdapter.WriteImgRcvViewHolder>(DifferCallback.uriDifferCallback) {

    inner class WriteImgRcvViewHolder(binding: RcvItemWriteImgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val selectedImg : ImageView = binding.writeListImgSelected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteImgRcvViewHolder {
        return WriteImgRcvViewHolder(
            RcvItemWriteImgBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WriteImgRcvViewHolder, position: Int) {
        val item = currentList[position]
        holder.itemView.setOnClickListener {
            itemClick?.imgClick(item)
        }
        holder.selectedImg.load(currentList[position])
    }
}