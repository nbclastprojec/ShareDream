package com.dreamteam.sharedream.home.Search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.WriteItemBinding
import android.content.Context
import coil.load
import com.dreamteam.sharedream.model.PostRcv
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SeachAdapter(
    private val context: Context,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<SeachAdapter.SearchHolder>() {

    interface OnItemClickListener {
        fun onItemClick(post: PostRcv)
    }

    private var searchDataItem: MutableList<PostRcv> = ArrayList()


    // 외부에서 데이터 설정하는 함수

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WriteItemBinding.inflate(inflater, parent, false)
        return SearchHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val post = searchDataItem[position]
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(post)
        }


        val searchItem = searchDataItem[position]
        val searchHolder = holder as SeachAdapter.SearchHolder
//        val storage = Firebase.storage
//        val fileName = searchItem.imgs.first()
//        val storageRef = storage.getReference("post").child(fileName.toString())
//        val downloadTask = storageRef.downloadUrl
//
//        downloadTask.addOnSuccessListener { uri ->
//
//            Glide.with(context)
//                .load(uri)
//                .into(searchHolder.image)
//        }.addOnFailureListener {
//            Log.e("HomeAdpate", "nyh Glade imageDownload fail homeitem.image =  ${searchItem.imgs}")
//            Log.e("HomeAdpate", "nyh Glade imageDownload fail it =  $it")
//            Log.d("nyh", "onBindViewHolder: $storageRef")
//            Log.d("nyh", "onBindViewHolder: $fileName")
//        }

        searchHolder.binde(post)

        searchHolder.title.text = searchItem.title
        searchHolder.subtitle.text = searchItem.desc
        searchHolder.category.text = searchItem.category
        searchHolder.value.text = searchItem.price.toString()
        Log.d("nyh", "onBindViewHolder: $searchItem")
    }

    override fun getItemCount(): Int {
        return searchDataItem.size
    }

    inner class SearchHolder(val binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.writeTittle
        val subtitle = binding.writeSubtittle
        val value = binding.writePrice
        val category = binding.writeCategory
        val image = binding.writeImage

        fun binde(post: PostRcv){
            if(post.imgs.isNotEmpty()) {
                val imageUrl = post.imgs.first()
                image.load(imageUrl)
            }
        }
    }

    fun setData(data: List<PostRcv>) {
        searchDataItem.clear()
        if (data.isNotEmpty()) {
            searchDataItem.addAll(data)
            Log.d("nyh", "setDataSearchAdapter: 데이터가 ${data.size}개의 항목으로 설정되었습니다.")
        } else {
            Log.d("nyh", "setDataSearchAdapter: 데이터가 비어 있습니다.")
        }
        notifyDataSetChanged() // 어댑터에 데이터 변경을 알립니다
    }
}