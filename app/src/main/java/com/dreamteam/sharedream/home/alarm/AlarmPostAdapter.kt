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
import com.dreamteam.sharedream.home.Search.SeachAdapter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date


class AlarmPostAdapter(
    private val context: Context,
    private val itemClickListener: AlarmPostAdapter.OnItemClickListener
) :
    RecyclerView.Adapter<AlarmPostAdapter.AlarmHolder>() {
    private var alarmItem: List<AlarmPost?> = emptyList()
    interface OnItemClickListener {
        fun onItemClick(post: AlarmPost)
    }
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

        fun bind(post: AlarmPost, timestamp: Timestamp) {

            val date: Date = timestamp.toDate()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")

//            postDate.text = dateFormat.format(date)

            // 2. 현재 날짜, 시간 기준으로 만들기 ( 1시간 전, 2일 전, 1달 전)
            val currentDateTime: Date = Date()
            val diff: Long = currentDateTime.time - date.time
            // 분 단위 차이
            val minutes: Long = diff / (1000 * 60)
            val hours: Long = minutes / 60
            val day: Long = hours / 24
            val week: Long = day / 7
            val month: Long = day / 31
            val year: Long = month / 12

            val result: String =
                when {
                    minutes < 1 -> "방금 전"
                    minutes < 60 -> "${minutes}분 전"
                    hours < 24 -> "${hours}시간 전"
                    day in 1..6 -> "${day}일 전"
                    day in 7..13 -> "지난 주"
                    day in 14..30 -> "${week}주 전"
                    month in 1..12 -> "${month}달 전"
                    year in 1..100 -> "${year}년 전"
                    else -> "${dateFormat.format(date)}"
                }
            alarmTime.text = result
            alarmTitle.text = post.title
            alarmNickname.text = post.nickname

            if (post.imgs.isNotEmpty()) {
                val imageUrl = post.imgs.first()
                alarmImage.load(imageUrl)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun deleteItem() {
            val post = alarmItem[position]
            if (post != null) {
                val myDocuId = post.myDocuId

                if (alarmItem is MutableList) {
                    (alarmItem as MutableList).removeAt(position)
                }
                notifyItemRemoved(position)

                val db = Firebase.firestore
                db.collection("notifyList")
                    .document(myDocuId)
                    .delete()
                    .addOnSuccessListener {
                        notifyDataSetChanged()
                        Log.d("nyh", "deleteItem: 삭제완료")
                        Log.d("nyh", "deleteItem: $myDocuId")
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
        holder.itemView.setOnClickListener {
            if (post != null) {
                itemClickListener.onItemClick(post)
            }
        }

        if (post != null) {
            holder.bind(post, post.timestamp)
            Log.d("nyh", "onBindViewHolder: $alarmItem")
        } else {
            Log.e("nyh", "onBindViewHolder: Data is empty at position $position")
        }
    }
}