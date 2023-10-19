package com.dreamteam.sharedream.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.firestore.FirebaseFirestore

class HomeAdapter(private val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var homeDataItem: ArrayList<PostData> = ArrayList()
    fun postDataFromFirestore() {

        val fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Post").get()
            .addOnSuccessListener { result ->

                Log.d("postDataFromFirestore", "nyh postDataFromFirestore suc: $result")

                val newData = mutableListOf<PostData>()
                for (i in result) {
                    if (i.exists()) {
                        Log.d("postDataFromFirestore", "nyh postDataFromFirestore suc: ${newData.size}, $newData")
                        val postData = i.toObject(PostData::class.java)
                        newData.add(postData)
                    }
                }
                homeDataItem.clear()
                homeDataItem.addAll(newData)
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->

                Log.e("FirestoreAdapter", "Error getting documents: $e")
            }
    }

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

//        Glide.with(context)
//            .load(homeItem.image)
//            .into(homeHolder.image)

        homeHolder.title.text = homeItem.title
        homeHolder.subtitle.text = homeItem.mainText
        homeHolder.category.text = homeItem.category
        homeHolder.value.text = homeItem.value.toString()

    }

    inner class HomeHolder(val binding : WriteItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        val title  = binding.writeTittle
        val subtitle = binding.writeSubtittle
        val value = binding.writePrice
        val category = binding.writeCategory
    }
}