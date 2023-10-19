package com.dreamteam.sharedream.home.Edit

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.EditImageItemBinding

class EditImageAdapter(val context: EditActivity, val items: ArrayList<Uri>) :
    RecyclerView.Adapter<EditImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EditImageItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    inner class ViewHolder(private val binding: EditImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(item: Uri) {
            val imageArea = binding.editImage
            Glide.with(context)
                .load(item)
                .into(imageArea)
        }
    }
}