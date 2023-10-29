package com.dreamteam.sharedream.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID


@Suppress("DEPRECATION")
class HomeAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var homeDataItem: ArrayList<PostData> = ArrayList()
    private var filteredDataItem: List<PostData> = ArrayList()
    private var itemClickListener: ((String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String) -> Unit) {
        itemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun postDataFromFirestore() {

        val fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Posts").get()
            .addOnSuccessListener { result ->

                Log.d("postDataFromFirestore", "nyh postDataFromFirestore suc: $result")

                val newData = mutableListOf<PostData>()
                for (i in result) {
                    if (i.exists()) {
                        Log.d(
                            "postDataFromFirestore",
                            "nyh postDataFromFirestore suc: ${newData.size}, $newData"
                        )
                        val postData = i.toObject(PostData::class.java)
                        newData.add(postData)
                    }
                }

                homeDataItem.clear()
                homeDataItem.addAll(newData)
                //filterByCategory을 처음에 가져와서 여기서 써줘야 돌아간다!!
                filterByCategory("")
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->

                Log.e("FirestoreAdapter", "Error getting documents: $e")
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WriteItemBinding.inflate(inflater, parent, false)
        return HomeHolder(binding)
    }

    //필터 된 데이터 List를 Default로 Count학
    override fun getItemCount(): Int {
        return filteredDataItem.size
    }


    //선택되지 않았을 땐 전체데이터 설정, else는 filter
    @SuppressLint("NotifyDataSetChanged")
    fun filterByCategory(category: String) {
        if (category.isEmpty()) {
            Log.d("nyh", "filterByCategory: ${filteredDataItem.size}")
            Log.d("nyh", "filterByCategory: ${filteredDataItem}")
            filteredDataItem = homeDataItem // 전체 데이터 표시
        } else {
            filteredDataItem = homeDataItem.filter { it.category == category }
            Log.d("nyh", "filterByCategory else: ${filteredDataItem.size}")
            Log.d("nyh", "filterByCategory else : ${filteredDataItem}")
        }
        notifyDataSetChanged()
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val homeItem = filteredDataItem[position]
        val homeHolder = holder as HomeHolder
        val storage = Firebase.storage
        val fileName = homeItem.image
        val storageRef = storage.getReference("image").child(fileName)
        val downloadTask = storageRef.downloadUrl


       // holder.button.setOnClickListener {
        //            // 알림 제목과 내용 설정
        //            val notificationTitle = homeItem.title
        //            val notificationBody = "눌렀습니다"
        //            val uniqueMessageId = UUID.randomUUID().toString()
        //
        //            // FCM 알림에 추가할 데이터 설정
        //            val data = mutableMapOf<String, String>()
        //            data["key1"] = "value1"
        //            data["key2"] = "value2"
        //            FirebaseMessaging.getInstance().isAutoInitEnabled = true
        //
        //            // FCM 알림을 보내기 위한 데이터 설정
        //
        //
        //            val token = homeItem.token
        //
        //            val message = RemoteMessage.Builder(token)
        //                .setMessageId(uniqueMessageId)
        //                .setData(data) // 데이터 추가
        //                .addData("title", notificationTitle) // 알림 제목
        //                .addData("body", notificationBody)
        ////                        o.setT("cMXusJ_PMYJI1PLYFZKzOb:APA91bGCjRm5XELaDHs3kipLW2HrJDNFiwpsQ-cITbIM4FnM5AsJmZqqQs_cyJ93l-qImTtO20gdr62Q2jpeWWETObgDZwMtdhvgSBjYqUdkhYUPmHA-LOr4K9mTKpqbiTzQzQ7j_uzk")
        //                .build()
        //            FirebaseMessaging.getInstance().send(message)
        //            // FCMService의 sendNonotification 함수 호출
        //            val fcmService = FCMService()
        //            fcmService.sendNonotification(context, notificationTitle, notificationBody, data)
        //
        //
        //        }


            downloadTask.addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .into(homeHolder.image)
            }.addOnFailureListener {
                Log.e(
                    "HomeAdpate",
                    "nyh Glade imageDownload fail homeitem.image =  ${homeItem.image}"
                )
                Log.e("HomeAdpate", "nyh Glade imageDownload fail it =  $it")
                Log.d("nyh", "onBindViewHolder: $storageRef")
                Log.d("nyh", "onBindViewHolder: $fileName")
            }

            homeHolder.title.text = homeItem.title
            homeHolder.subtitle.text = homeItem.mainText
            homeHolder.category.text = homeItem.category
            homeHolder.value.text = homeItem.value.toString()

            homeHolder.itemView.setOnClickListener {

//                val sendValue = homeItem.value.toString()
//                val intent = Intent(context, DetailFrameActivity::class.java)
//                intent.putExtra("value", sendValue)
//                context.startActivity(intent)
//                Log.d("document12", "documentId:$sendValue")
            }

            Log.d("nyh", "onBindViewHolder: $homeItem")
        }


        inner class HomeHolder(val binding: WriteItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            val title = binding.writeTittle
            val subtitle = binding.writeSubtittle
            val value = binding.writePrice
            val category = binding.writeCategory
            val image = binding.writeImage
            //val button = binding.btnLike
        }
    }
