package com.dreamteam.sharedream.home.Search


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.WriteItemBinding
import android.content.Context
import com.dreamteam.sharedream.model.Post
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SeachAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var searchDataItem: List<Post> = ArrayList()


    // 외부에서 데이터 설정하는 함수

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WriteItemBinding.inflate(inflater, parent, false)
        return SearchHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val searchItem = searchDataItem[position]
        val searchHolder = holder as SeachAdapter.SearchHolder
        val storage = Firebase.storage
        val fileName = searchItem.imgs.first()
        val storageRef = storage.getReference("post").child(fileName)
        val downloadTask = storageRef.downloadUrl

        downloadTask.addOnSuccessListener { uri ->

            Glide.with(context)
                .load(uri)
                .into(searchHolder.image)
        }.addOnFailureListener {
            Log.e("HomeAdpate", "nyh Glade imageDownload fail homeitem.image =  ${searchItem.imgs}")
            Log.e("HomeAdpate", "nyh Glade imageDownload fail it =  $it")
            Log.d("nyh", "onBindViewHolder: $storageRef")
            Log.d("nyh", "onBindViewHolder: $fileName")
        }

        searchHolder.title.text = searchItem.title
        searchHolder.subtitle.text = searchItem.desc
        searchHolder.category.text = searchItem.category
        searchHolder.value.text = searchItem.price
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
    }
    fun setData(data: List<Post>) {
        if (data.isNotEmpty()) {
            searchDataItem = ArrayList(data)
            Log.d("nyh", "setDataSearchAdapter: Data is set with ${data.size} items.")
        } else {
            Log.d("nyh", "setDataSearchAdapter: Data is empty.")
        }
    }
}