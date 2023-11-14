package com.dreamteam.sharedream.home.alarm

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.databinding.MyalarmBinding
import com.dreamteam.sharedream.model.AlarmChatData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class AlarmAdapter2(
    private val context: Context,
//    private val itemClickListener: AlarmAdapter2.OnItemClickListener
) :
    RecyclerView.Adapter<AlarmAdapter2.AlarmHolder2>() {
    private var chatItem: List<AlarmChatData?> = emptyList()
//
//        interface OnItemClickListener {
//        fun onItemClick(post: AlarmChatData)
//    }
    fun setData(data: List<AlarmChatData?>) {
        chatItem = data
        notifyDataSetChanged()
        Log.d("nyh", "setData: $data")
    }

    inner class AlarmHolder2(val binding: MyalarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val alarmImage = binding.imageTrade
        val alarmTitle = binding.txtTradeTitle
        val alarmNickname = binding.notinickname
        val alarmTime = binding.txtTime

        fun bind(chatPost: AlarmChatData, timestamp: Timestamp) {

            val date: Date = timestamp.toDate()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//
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
            alarmTitle.text = "채팅신청이 도착했습니다."
            alarmNickname.text = chatPost.name

            if (chatPost.profileImageUrl?.isNotEmpty() == true) {
                val imageUrl = chatPost.profileImageUrl.first()
                alarmImage.load(imageUrl)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun deleteItem() {
            val post = chatItem[position]
            if (post != null) {
                val myDocuId = post.myDocuId

                if (chatItem is MutableList) {
                    (chatItem as MutableList).removeAt(position)
                }
                notifyItemRemoved(position)

                val db = Firebase.firestore
                if (myDocuId != null) {
                    db.collection("notifyChatList")
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
        }

        init {
            binding.btnDelete.setOnClickListener {
                deleteItem()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmHolder2 {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MyalarmBinding.inflate(inflater, parent, false)
        return AlarmHolder2(binding)
    }

    override fun getItemCount(): Int {
        return chatItem.size
    }

    override fun onBindViewHolder(holder: AlarmAdapter2.AlarmHolder2, position: Int) {
        val post = chatItem?.getOrNull(position)
//        holder.itemView.setOnClickListener {
//            if (post != null) {
//                itemClickListener.onItemClick(post)
//            }
//        }

        if (post != null && chatItem != null) {
            if (post != null) {
                holder.bind(post, post.timestamp)
            }
            Log.d("nyh", "onBindViewHolder: $chatItem")
        } else {
            Log.e("nyh", "onBindViewHolder: Data is empty at position $position")
        }
    }
}