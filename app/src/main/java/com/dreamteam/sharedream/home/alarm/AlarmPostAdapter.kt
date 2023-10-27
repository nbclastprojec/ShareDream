package com.dreamteam.sharedream.home.alarm

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.MyalarmBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AlarmPostAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var alarmItem: ArrayList<Post> = ArrayList()
    private val storage = Firebase.storage

    fun setData(data: MutableList<Post>){
        alarmItem = ArrayList(data)
        Log.d("nyh", "AlarmPostAdapter: $data")
    }

    inner class AlarmHolder(val binding: MyalarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val alarmImage = binding.imageTrade
        val alarmTitle = binding.txtTradeTitle
        val alarmCategory = binding.txtCategory
        val alarmValue = binding.txtValue
        fun binde(imagePath : String) {
            storage.reference.child("post").child("$imagePath").downloadUrl.addOnSuccessListener { uri ->
                Glide.with(itemView)
                    .load(uri)
                    .into(alarmImage)
            }.addOnFailureListener {
                Log.d("xxxx", " adapter bind Failure $it")
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MyalarmBinding.inflate(inflater, parent, false)
        return AlarmHolder(binding)
    }

    override fun getItemCount(): Int {
        return alarmItem.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val alamItem = alarmItem[position]
        val alarmHolder = holder as AlarmPostAdapter.AlarmHolder


        holder.apply {
            alarmTitle.text = alamItem.title
            alarmCategory.text = alamItem.category
            alarmValue.text = alamItem.price
            binde(alamItem.imgs.first())
            Log.d("nyh", "onBindViewHolder: $alarmItem")
        }
    }
}