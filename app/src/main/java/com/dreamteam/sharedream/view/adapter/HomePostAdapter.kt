package com.dreamteam.sharedream.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.FCMService
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.Post
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage
import java.util.UUID


class HomePostAdapter(private val context: Context, private val postClick: PostClick, private val allPosts: List<Post>) :
    ListAdapter<Post, HomePostAdapter.HomePostRcvViewHolder>(DifferCallback.differCallback) {

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

        holder.bind(positionItem.imgs[0])
    }

    inner class HomePostRcvViewHolder(binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postTitle: TextView = binding.writeTittle
        val postDesc: TextView = binding.writeSubtittle
        val postPrice: TextView = binding.writePrice
        val postCategory: TextView = binding.writeCategory
        val postImg: ImageView = binding.writeImage
        val postheart:ImageView = binding.btnHeart


        fun bind(imagePath: String) {


            //todo 글라이드 캐싱 추가 예정
//                storage.reference.child("post").child("$imagePath").downloadUrl.addOnSuccessListener { uri ->
//                 캐싱 - rcv 자체적인 캐시 or 페이징?
//                    Log.d("xxxx", "bind: storage download uri after img - $imagePath")
//                    Glide.with(itemView)
//                        .load(uri)
//                        .into(postImg)
//                    this.imagePath = imagePath
//                }
//                    .addOnFailureListener {
//                        Log.d("xxxx", " adapter bind Failure $it")
//                    }
//            } else {
//                Glide.with(itemView)
//                    .load(uri)
//                    .into(postImg)
//                this.imagePath = imagePath
//            }
            Log.d("xxxx", "bind: storage download uri before img - ${imagePath}")
            storage.reference.child("post")
                .child("$imagePath").downloadUrl.addOnSuccessListener { uri ->
//                 캐싱 - rcv 자체적인 캐시 or 페이징?
                Log.d("xxxx", "bind: storage download uri after img - $imagePath")
                Glide.with(itemView)
                    .load(uri)
                    .into(postImg)

            }
                .addOnFailureListener {
                    Log.d("xxxx", " adapter bind Failure $it")
                }
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