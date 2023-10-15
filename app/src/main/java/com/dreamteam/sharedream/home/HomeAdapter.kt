package com.dreamteam.sharedream.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.HomeData

class HomeAdapter(private val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var homeDataItem: ArrayList<HomeData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WriteItemBinding.inflate(inflater,parent,false)
        return HomeHolder(binding)
    }

    override fun getItemCount(): Int {
        return homeDataItem.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val homeItem = homeDataItem[position]
        val homeHolder = holder as HomeHolder

        Glide.with(context)
            .load(homeItem.image)
            .into(homeHolder.image)

        homeHolder.title.text = homeItem.title
        homeHolder.subtitle.text = homeItem.mainText
        homeHolder.category.text = homeItem.category

    }

    inner class HomeHolder(val binding : WriteItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        val title  = binding.writeTittle
        val subtitle  = binding.writeSubtittle
        val category = binding.writeCategory
        val date = binding.writeDate
        val image = binding.writeImage
    }
    fun sethome(newVideos: List<HomeData>) {
        homeDataItem.clear()
        if (newVideos.isNotEmpty()) {
            homeDataItem.addAll(newVideos)
        }
        notifyDataSetChanged()
    }
}