package com.dreamteam.sharedream.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.DetailFrameActivity
import com.dreamteam.sharedream.databinding.WriteItemBinding
import com.dreamteam.sharedream.model.PostData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


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
        fireStore.collection("Post").get()
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val homeItem = filteredDataItem[position]
        val homeHolder = holder as HomeHolder
        val storage = Firebase.storage
        val fileName = homeItem.image
        val storageRef = storage.getReference("image").child(fileName)
        val downloadTask = storageRef.downloadUrl

        downloadTask.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .into(homeHolder.image)
        }.addOnFailureListener {
            Log.e("HomeAdpate", "nyh Glade imageDownload fail homeitem.image =  ${homeItem.image}")
            Log.e("HomeAdpate", "nyh Glade imageDownload fail it =  $it")
            Log.d("nyh", "onBindViewHolder: $storageRef")
            Log.d("nyh", "onBindViewHolder: $fileName")
        }

        homeHolder.title.text = homeItem.title
        homeHolder.subtitle.text = homeItem.mainText
        homeHolder.category.text = homeItem.category
        homeHolder.value.text = homeItem.value.toString()

        homeHolder.itemView.setOnClickListener {


            val sendValue = homeItem.value.toString()
            val intent = Intent(context, DetailFrameActivity::class.java)
            intent.putExtra("value", sendValue)
            context.startActivity(intent)
            Log.d("document12","documentId:$sendValue")
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
    }
}