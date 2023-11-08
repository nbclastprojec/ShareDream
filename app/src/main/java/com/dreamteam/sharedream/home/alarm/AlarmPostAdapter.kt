package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.databinding.MyalarmBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AlarmPostAdapter(private val context: Context) :
    RecyclerView.Adapter<AlarmPostAdapter.AlarmHolder>() {
    private var alarmItem: List<AlarmPost?> = emptyList()

    fun setData(data: List<AlarmPost?>) {
        alarmItem = data
        notifyDataSetChanged()
        Log.d("nyh", "setData: $data")
    }

    @Suppress("DEPRECATION")
    inner class AlarmHolder(val binding: MyalarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val alarmImage = binding.imageTrade
        val alarmTitle = binding.txtTradeTitle
        val alarmNickname = binding.notinickname
        val alarmTime = binding.txtTime

        fun bind(post: AlarmPost) {
            alarmTitle.text = post.title
            alarmNickname.text = post.nickname
            alarmTime.text = post.timestamp.toDate().time.toString()

            if (post.imgs.isNotEmpty()) {
                val imageUrl = post.imgs.first()
                alarmImage.load(imageUrl)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun deleteItem() {
            val post = alarmItem[position]
            if (post != null) {
                val documentId = post.documentId

                if (alarmItem is MutableList) {
                    (alarmItem as MutableList).removeAt(position)
                }
                notifyItemRemoved(position)

                val db = Firebase.firestore
                db.collection("notifyList")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        notifyDataSetChanged()
                        Log.d("nyh", "deleteItem: 삭제완료")
                        Log.d("nyh", "deleteItem: $documentId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("nyh", "deleteItem: $e")
                    }
            }
        }

        init {
            binding.btnDelete.setOnClickListener {
                deleteItem()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MyalarmBinding.inflate(inflater, parent, false)
        return AlarmHolder(binding)
    }

    override fun getItemCount(): Int {
        return alarmItem.size
    }

    override fun onBindViewHolder(holder: AlarmHolder, position: Int) {
        val post = alarmItem?.getOrNull(position)

        if (post != null) {
            holder.bind(post)
            Log.d("nyh", "onBindViewHolder: $alarmItem")
        } else {
            Log.e("nyh", "onBindViewHolder: Data is empty at position $position")
        }
    }
}