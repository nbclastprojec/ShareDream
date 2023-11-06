package com.dreamteam.sharedream.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.ActivityChatBinding
import com.dreamteam.sharedream.databinding.ChatItemBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class ChatMessageActivity : AppCompatActivity() {

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomuid : String? = null
    private var destinationUid : String? = null
    private var uid : String? = null
    private var recyclerView : RecyclerView? = null
    private val storage = Firebase.storage
    private lateinit var binding : ActivityChatBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root

        val store = FirebaseFirestore.getInstance()
        val intentDestinationUid = intent.getStringExtra("destinationUid").toString()


       destinationUid = intentDestinationUid




        store.collection("UserData").document(destinationUid!!)
            .get()
            .addOnSuccessListener { documents ->
                if(documents != null){
                    val name = documents.getString("nickname")
                    binding.chat.text = name
                }
            }

        val imageView = binding.chatSendBtn
        val editText = binding.chattext

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월 dd일 hh:mm")
        val realTime = dateFormat.format(Date(time)).toString()
        val backbtn = binding.backButtonChat

        setContentView(view)



        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = binding.chatRecycleView

        imageView.setOnClickListener{
            Log.d("susu", "$destinationUid")
            val chatModel = ChatModel()
            chatModel.users.put(uid.toString(),true)
            chatModel.users.put(destinationUid!!,true)

            val comment = ChatModel.Comment(uid, editText.text.toString(), realTime)
            if(chatRoomuid == null) {
                imageView.isEnabled = false
                fireDatabase.child("ChatRoom").push().setValue(chatModel).addOnSuccessListener {
                    checkChatRoom()
                    Handler().postDelayed({
                        fireDatabase.child("ChatRoom").child(chatRoomuid.toString()).child("comments").push().setValue(comment)
                        editText.text = null
                    }, 1000L)

                }
            }else{
                fireDatabase.child("ChatRoom").child(chatRoomuid.toString()).child("comments").push().setValue(comment)
                editText.text = null

            }
        }

        backbtn.setOnClickListener{
            super.onBackPressed()
        }

        checkChatRoom()



    }
    private fun checkChatRoom() {
        fireDatabase.child("ChatRoom").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        val chatModel = item.getValue<ChatModel>()
                        if (chatModel?.users!!.containsKey(destinationUid)) {
                            chatRoomuid = item.key
                            recyclerView?.layoutManager = LinearLayoutManager(this@ChatMessageActivity)
                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<ChatModel.Comment>()
        private var chat : Chatting? = null
        init{
            fireDatabase.child("users").child(destinationUid.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    chat = snapshot.getValue<Chatting>()
                    chat?.name = binding.chat.toString()
                    getMessageList()
                }
            })
        }

        fun getMessageList(){
            fireDatabase.child("ChatRoom").child(chatRoomuid.toString()).child("comments").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for(data in snapshot.children){
                        val item = data.getValue<ChatModel.Comment>()
                        comments.add(item!!)
                    }
                    notifyDataSetChanged()
                    //메세지를 보낼 시 화면을 맨 밑으로 내림
                    recyclerView?.scrollToPosition(comments.size - 1)
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val itemBinding = ChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

            return MessageViewHolder(itemBinding)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val comment = comments[position]
            holder.bind(comment)
        }

        inner class MessageViewHolder(private val itemBinding: ChatItemBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            val message = itemBinding.chatMessage
            val name: TextView = itemBinding.chatName
            val profile: ImageView = itemBinding.chatImage
            val destination: LinearLayout = itemBinding.messageItemLayoutDestination
            val layoutMain: LinearLayout = itemBinding.messageItemLinearlayoutMain
            val time: TextView = itemBinding.chatDate

            fun bind(comment: ChatModel.Comment) {
                with(itemBinding) {
                    message.text = comment.message
                    time.text = comment.time
                    if (comment.uid == uid) {
                        message.setBackgroundResource(R.drawable.rightbubble)
                        name.visibility = View.INVISIBLE
                        layoutMain.gravity = Gravity.RIGHT
                    } else {
                        Glide.with(itemView.context)
                            .load(chat?.profileImageUrl)
                            .into(profile)
                        message.setBackgroundResource(R.drawable.leftbubble)
                        name.text = chat?.name
                        destination.visibility = View.VISIBLE
                        name.visibility = View.VISIBLE
                        layoutMain.gravity = Gravity.LEFT
                    }
                }
            }
        }


        override fun getItemCount(): Int {
            return comments.size
        }


    }

        }


