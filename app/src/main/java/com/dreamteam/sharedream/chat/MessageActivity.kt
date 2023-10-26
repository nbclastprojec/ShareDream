package com.dreamteam.sharedream.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dreamteam.sharedream.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.log

class MessageActivity : AppCompatActivity() {

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomuid : String? = null
    private var destinationUid : String? = null
    private var uid : String? = null
    private var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val imageView = findViewById<ImageView>(R.id.chatSendBtn)
        val editText = findViewById<TextView>(R.id.chattext)

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월 dd일 hh:mm")
        val realTime = dateFormat.format(Date(time)).toString()

        destinationUid = intent.getStringExtra("destinationUid")
        Log.d("susu", "${destinationUid}")

        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.chat_recycleView)

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
                           // messageActivity_ImageView.isEnabled = true
                            recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<ChatModel.Comment>()
        private var friend : Friend? = null
        init{
            fireDatabase.child("users").child(destinationUid.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    friend = snapshot.getValue<Friend>()
                    //chat.text = friend?.name
                    getMessageList()
                }
            })
        }

        fun getMessageList(){
            fireDatabase.child("chatrooms").child(chatRoomuid.toString()).child("comments").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for(data in snapshot.children){
                        val item = data.getValue<ChatModel.Comment>()
                        comments.add(item!!)
                        println(comments)
                    }
                    notifyDataSetChanged()
                    //메세지를 보낼 시 화면을 맨 밑으로 내림
                    recyclerView?.scrollToPosition(comments.size - 1)
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)

            return MessageViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.message.textSize = 20F
            holder.message.text = comments[position].message
            holder.time.text = comments[position].time
            if(comments[position].uid.equals(uid)){ // 본인 채팅

                holder.name.visibility = View.INVISIBLE
                holder.destination.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            }else{ // 상대방 채팅
                Glide.with(holder.itemView.context)
                    .load(friend?.profileImageUrl)
                    .into(holder.profile)
                holder.name.text = friend?.name
                holder.destination.visibility = View.VISIBLE
                holder.name.visibility = View.VISIBLE
                holder.layout_main.gravity = Gravity.LEFT
            }
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val message: TextView = view.findViewById(R.id.chat_message)
            val name: TextView = view.findViewById(R.id.chat_name)
            val profile: ImageView = view.findViewById(R.id.chat_image)
            val destination: LinearLayout = view.findViewById(R.id.messageItem_layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val time : TextView = view.findViewById(R.id.chat_date)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }
}


