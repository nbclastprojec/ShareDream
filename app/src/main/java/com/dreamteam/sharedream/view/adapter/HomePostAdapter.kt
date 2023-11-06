package com.dreamteam.sharedream.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dreamteam.sharedream.adapter.DifferCallback
import com.dreamteam.sharedream.adapter.PostClick
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.PostRcv
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class HomePostAdapter(
    private val context: Context,
    private val postClick: PostClick,
    private var allPosts: List<PostRcv>
) :
    ListAdapter<PostRcv, HomePostAdapter.HomePostRcvViewHolder>(DifferCallback.differCallback) {

    private val storage = Firebase.storage
    private val allItems = allPosts
    private var db = Firebase.firestore

    init {
        db.collection("posts").orderBy("timestamp")
    }

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

        holder.itemView.setOnClickListener {
            postClick?.postClick(post)
        }

        val positionItem = currentList[position]
        holder.apply {
            postCategory.text = "카테고리 : ${positionItem.category}"
            postTitle.text = positionItem.title
            postDesc.text = positionItem.desc
            postPrice.text = positionItem.price.toString()

            postEndDate.text = EndTime(positionItem.endDate)
            postPrice.text = "${positionItem.price}원"



        }

        holder.bind(positionItem.imgs[0], positionItem.timestamp)
    }
    fun sortPriceAsc(){
        val sortedItems = currentList.sortedBy { it.price }
        submitList(sortedItems)
    }
    fun sortPriceDesc(){
        val sortedItems =  currentList.sortedByDescending { it.price }
        submitList(sortedItems)
    }
    fun sortLikeAsc() {
        val sortedItem = currentList.sortedBy { it.likeUsers.size }
        submitList(sortedItem)
    }

    fun filteredPrice(minPrice:Long, maxPrice: Long){
        val filteredList = currentList.filter { it.price in minPrice .. maxPrice}
        submitList(filteredList)
    }


    inner class HomePostRcvViewHolder(binding: WriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postTitle: TextView = binding.writeTittle
        val postDesc: TextView = binding.writeSubtittle
        val postPrice: TextView = binding.writePrice
        val postCategory: TextView = binding.writeCategory
        val postImg: ImageView = binding.writeImage
//        val postheart: ImageView = binding.btnHeart
        val postDate: TextView = binding.writePageDate
        val postEndDate:TextView=binding.endDate

        val postStateBgClosed: ImageView = binding.itemImgStateClosed
        val postStateBgPutOff: ImageView = binding.itemImgStatePutOff
        val postStateBgReservation : ImageView = binding.itemImgStateReservation


        fun bind(imagePath: Uri, timestamp: Timestamp) {
            postImg.load(imagePath)

            when (currentList[position].state){
                "교환 가능" -> {
                    postStateBgClosed.visibility = View.INVISIBLE
                    postStateBgPutOff.visibility = View.INVISIBLE
                    postStateBgReservation.visibility = View.INVISIBLE
                }
                "교환 보류" -> postStateBgPutOff.visibility = View.VISIBLE
                "예약 중" -> postStateBgReservation.visibility = View.VISIBLE
                "교환 완료" -> postStateBgClosed.visibility = View.VISIBLE
                else -> return
            }

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
            postDate.text = result

        }
    }



    private fun EndTime(endTime: String): String {
        val dateParts = endTime.split("~")

        if (dateParts.size == 2) {
            val formattedDate = dateParts[1].trim()

            val dateFormat = SimpleDateFormat("EEE MM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)

            try {
                val futureDate = dateFormat.parse(formattedDate)
                val currentDate = Date()
                val diff = futureDate.time - currentDate.time
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

                return when {
                    days >= 1 -> "$days 일 남음"
                    hours >= 1 -> "$hours 시간 남음"
                    minutes >= 0 -> "$minutes 분 남음"
                    else -> "마감되었습니다!"
                }
            } catch (e: ParseException) {
                return "날짜 형식 오류"
            }
        } else {
            return "날짜 형식 오류"
        }
    }
    fun setData(data:List<PostRcv>){
        if(data.isNotEmpty()){
            allPosts = ArrayList(data)
        }
    }
}