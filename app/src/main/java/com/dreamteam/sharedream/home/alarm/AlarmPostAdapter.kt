package com.dreamteam.sharedream.home.alarm

import AlarmPost
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.MyalarmBinding
import com.dreamteam.sharedream.home.Search.SeachAdapter
import com.dreamteam.sharedream.model.AlarmChatData
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
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ALARM = 0
    private val TYPE_CHAT = 1

    private var alarmItem: List<AlarmPost?> = emptyList()
    private var chatItems: List<AlarmChatData?> = emptyList()

    interface OnItemClickListener {
        fun onAlarmItemClick(post: AlarmPost)
    }

    fun setData(data: List<AlarmPost?>) {
        alarmItem = data
        notifyDataSetChanged()
    }

    fun setChatData(data: List<AlarmChatData?>) {
        chatItems = data
        notifyDataSetChanged()
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

            binding.root.setOnClickListener {
                itemClickListener.onAlarmItemClick(post)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun deleteItem(position: Int) {
            if (position < alarmItem.size) {
                val post = alarmItem[position] ?: return
                val myDocuId = post.myDocuId

                val mutableAlarmItem = alarmItem.toMutableList()
                mutableAlarmItem.removeAt(position)
                alarmItem = mutableAlarmItem

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
                Log.d("nyh", "deleteItem: ")
                deleteItem(adapterPosition)
            }
        }
    }

    @Suppress("DEPRECATION")
    inner class AlarmChatHolder(val binding: MyalarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val alarmImage = binding.imageTrade
        val alarmTitle = binding.txtTradeTitle
        val alarmNickname = binding.notinickname
        val alarmbody = binding.txtBody
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
            alarmTitle.text = chatPost.title
            alarmbody.text = "님께서 채팅을 보냈습니다."
            alarmNickname.text = chatPost.nickname

            if (chatPost.profileImageUrl?.isNotEmpty() == true) {
                val imageUrl = chatPost.profileImageUrl
                alarmImage.load(imageUrl)
            } else {
                alarmImage.setImageResource(R.drawable.profile_circle)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun deleteItem(position: Int) {
            val chatPosition = position - alarmItem.size
            if (chatPosition < chatItems.size) {
                val post = chatItems[position] ?: return
                    val myDocuId = post.myDocuId

                val mutableChatItems = chatItems.toMutableList()
                mutableChatItems.removeAt(chatPosition)
                chatItems = mutableChatItems

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
                Log.d("nyh chat", "deleteItem: ")
                deleteItem(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ALARM -> {
                val binding = MyalarmBinding.inflate(inflater, parent, false)
                AlarmHolder(binding)
            }

            TYPE_CHAT -> {
                val binding = MyalarmBinding.inflate(inflater, parent, false)
                AlarmChatHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return alarmItem.size + chatItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < alarmItem.size) {
            TYPE_ALARM
        } else {
            TYPE_CHAT
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < alarmItem.size) {
            (holder as AlarmHolder).bind(alarmItem[position]!!, alarmItem[position]!!.timestamp)
        } else {
            (holder as AlarmChatHolder).bind(
                chatItems[position - alarmItem.size]!!,
                chatItems[position - alarmItem.size]!!.timestamp
            )
        }
    }
}