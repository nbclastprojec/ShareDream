package com.dreamteam.sharedream.home

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date

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
    private fun imageDownload() {
        val storage = Firebase.storage
        val storageRef = storage.getReference("image")
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mountainRef = storageRef.child("${fileName}.png")

        val downloadTask = mountainRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->

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
        val storage = Firebase.storage
        val storageRef = storage.getReference("image")
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mountainRef = storageRef.child("${fileName}.png")
        val downloadTask = mountainRef.downloadUrl

        downloadTask.addOnSuccessListener { uri ->

            Glide.with(context)
                .load(homeItem.image.toUri())
                .into(homeHolder.image)
        }.addOnFailureListener {
            Log.e("HomeAdpate", "nyh imageDownload fail")
        }

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
        val image = binding.writeImage
    }
}