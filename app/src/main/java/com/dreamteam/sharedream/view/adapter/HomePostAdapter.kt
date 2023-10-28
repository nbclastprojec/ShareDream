package com.dreamteam.sharedream.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.FCMService
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.model.PostRcv
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID


class HomePostAdapter(private val context: Context, private val postClick: PostClick, private val allPosts: List<PostRcv>) :
    ListAdapter<PostRcv, HomePostAdapter.HomePostRcvViewHolder>(DifferCallback.differCallback) {

    private val storage = Firebase.storage
    private val allItems = allPosts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostRcvViewHolder {
        return HomePostRcvViewHolder(
            WriteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HomePostRcvViewHolder, position: Int) {
        val post = currentList[position]
        val allpost = allPosts[position]
         holder.postheart.setOnClickListener {
                    // 알림 제목과 내용 설정
                    val notificationTitle = allpost.title
                    val notificationBody = "눌렀습니다"
                    val uniqueMessageId = UUID.randomUUID().toString()

                    // FCM 알림에 추가할 데이터 설정
                    val data = mutableMapOf<String, String>()
                    data["key1"] = "value1"
                    data["key2"] = "value2"
                    FirebaseMessaging.getInstance().isAutoInitEnabled = true

                    // FCM 알림을 보내기 위한 데이터 설정


                    val token = allpost.token

                    val message = RemoteMessage.Builder(token)
                        .setMessageId(uniqueMessageId)
                        .setData(data) // 데이터 추가
                        .addData("title", notificationTitle) // 알림 제목
                        .addData("body", notificationBody)
        //                        o.setT("cMXusJ_PMYJI1PLYFZKzOb:APA91bGCjRm5XELaDHs3kipLW2HrJDNFiwpsQ-cITbIM4FnM5AsJmZqqQs_cyJ93l-qImTtO20gdr62Q2jpeWWETObgDZwMtdhvgSBjYqUdkhYUPmHA-LOr4K9mTKpqbiTzQzQ7j_uzk")
                        .build()
                    FirebaseMessaging.getInstance().send(message)
                    // FCMService의 sendNonotification 함수 호출
                    val fcmService = FCMService()
                    fcmService.sendNonotification(context, notificationTitle, notificationBody, data)


                }
        holder.itemView.setOnClickListener {
            postClick?.postClick(post)
        }

        val positionItem = currentList[position]
        holder.apply {
            postCategory.text = "카테고리 : ${positionItem.category}"
            postTitle.text = positionItem.title
            postDesc.text = positionItem.desc
            postPrice.text = positionItem.price

        }

        holder.bind(positionItem.imgs[0],positionItem.timestamp)
    }

    inner class HomePostRcvViewHolder(binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postTitle: TextView = binding.writeTittle
        val postDesc: TextView = binding.writeSubtittle
        val postPrice: TextView = binding.writePrice
        val postCategory: TextView = binding.writeCategory
        val postImg: ImageView = binding.writeImage
        val postheart:ImageView = binding.btnHeart
        val postDate : TextView = binding.writePageDate


        fun bind(imagePath: Uri,timestamp: Timestamp) {
            postImg.load(imagePath)
            val date: Date = timestamp.toDate()
            // 1. 날짜 형식으로 만들기
            // timestamp를 Date 객체로 변환

            // SimpleDateFormat으로 원하는 형식으로 변환
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")

//            postDate.text = dateFormat.format(date)

            // 2. 현재 날짜, 시간 기준으로 만들기 ( 1시간 전, 2일 전, 1달 전)
            val currentDateTime: Date = Date()
            val diff: Long = currentDateTime.time - date.time
            // 분 단위 차이
            val minutes : Long = diff / (1000 * 60)
            val hours : Long = minutes / 60
            val day : Long = hours / 24
            val week : Long = day / 7
            val month : Long = day / 31
            val year : Long = month / 12

            val result: String =
                when {
                    minutes < 1 -> "방금 전"
                    minutes < 60 -> "${minutes}분 전"
                    hours < 24 -> "${hours}시간 전"
                    day in 1..6 -> "${day}일 전"
                    day in 7 .. 13 ->"지난 주"
                    day in 14..30 -> "${week}주 전"
                    month in 1..12 -> "${month}달 전"
                    year in 1 .. 100 -> "${year}년 전"
                    else -> "${dateFormat.format(date)}"
                }
            postDate.text = result

        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun onCategorySelected(category: String) {

        if (category.isEmpty()) {
            submitList(allItems)
        } else {
            // 카테고리에 따라 게시물을 필터링하고 어댑터를 업데이트합니다.
            val filteredList = allItems.filter { it.category == category }
            submitList(filteredList)
        }
        notifyDataSetChanged()
    }
}