package com.dreamteam.sharedream.chat

import android.content.Context
import android.content.Intent
import android.net.ParseException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.ChattingroomItemBinding
import com.dreamteam.sharedream.databinding.FragmentChattingroomBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TreeMap

class ChatFragment : Fragment() {

    private lateinit var binding : FragmentChattingroomBinding
    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChattingroomBinding.inflate(inflater,container,false)
        val view = binding.root


        val recyclerView = binding.chatfragmentRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()

        binding.backButtonChattingroompage.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
        private val chatModel = ArrayList<ChatModel>()
        private var uid : String? = null
        private val destinationUsers : ArrayList<String> = arrayListOf()

        init {

            uid = Firebase.auth.currentUser?.uid.toString()

            fireDatabase.child("ChatRoom").orderByChild("users/${uid}").equalTo(true)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            chatModel.clear()
                            for (data in snapshot.children){
                                chatModel.add(data.getValue<ChatModel>()!!)

                            }
                            notifyDataSetChanged()
                        }
                    }
                )
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


            val itemBinding = ChattingroomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(itemBinding)
        }

        inner class ViewHolder(private val itemBinding: ChattingroomItemBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            val imageView: ImageView = itemBinding.chattingroomProfile
            val time : TextView = itemBinding.chattingDate
            val tittle: TextView = itemBinding.chattingName
            val lastMessage: TextView = itemBinding.chattingMessage
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var destinationUid: String? = null



            for (user in chatModel[position].users.keys) {
                if (!user.equals(uid)) {
                    destinationUid = user
                    Log.d("susu", "onBindViewHolder: ${destinationUid}")
                    destinationUsers.add(destinationUid)

                    firestore.collection("UserData").document(destinationUid)
                    .get()
                        .addOnSuccessListener { documents ->

                                val name = documents.getString("nickname")

                                holder.tittle.text = name

                            Log.d("susu", "name: ${name}")

                        }
                        .addOnFailureListener { exception ->
                            Log.d("susu", "Error getting documents: $exception")
                        }

                    val storageReference = destinationUid?.let { storage.reference.child("ProfileImg").child(it) }
                    storageReference?.downloadUrl?.addOnSuccessListener { uri ->
                        Glide.with(holder.itemView.context)
                            .load(uri)
                            .into(holder.imageView)
                    }?.addOnFailureListener { exception ->
                        Log.e("ChatFragment", "이미지 다운로드 실패: ${exception.message}")
                    }
                }

            }






            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져옴
            val commentMap = TreeMap<String, ChatModel.Comment>(reverseOrder())
            commentMap.putAll(chatModel[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            val inputFormat = SimpleDateFormat("MM월 dd일 HH:mm")
            val outputFormat = SimpleDateFormat("MM월 dd일 HH:mm")
            val time = chatModel[position].comments[lastMessageKey]?.time
            val parsedDate = time?.let { inputFormat.parse(it) }
            val formattedDate = parsedDate?.let { outputFormat.format(it) }

            holder.lastMessage.text = chatModel[position].comments[lastMessageKey]?.message
            holder.time.text = formattedDate



            //채팅창 선책 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ChatMessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                context?.startActivity(intent)
            }

        }


        override fun getItemCount(): Int {
            return chatModel.size
        }
    }



}