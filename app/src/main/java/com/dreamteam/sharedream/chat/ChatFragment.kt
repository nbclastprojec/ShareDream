package com.dreamteam.sharedream.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.TreeMap

class ChatFragment : Fragment() {

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
        private val fireDatabase = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chattingroom,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chatfragment_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()
        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>(){
        private val chatModel = ArrayList<ChatModel>()
        private var uid : String? = null
        private val destinationUsers : ArrayList<String> = arrayListOf()

        init {
            uid = Firebase.auth.currentUser?.uid.toString()

            fireDatabase.child("ChatRoom").orderByChild("users/${uid}").equalTo(true).addListenerForSingleValueEvent(
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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {


            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.chattingroom_item, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.chattingroom_profile)
            val textView_title : TextView = itemView.findViewById(R.id.chatting_tittle)
            val textView_lastMessage : TextView = itemView.findViewById(R.id.chatting_message)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            var destinationUid: String? = null
            //채팅방에 있는 유저 모두 체크
            for (user in chatModel[position].users.keys) {
                if (!user.equals(uid)) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            fireDatabase.child("users").child("$destinationUid").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val friend = snapshot.getValue<Chatting>()
                    Glide.with(holder.itemView.context).load(friend?.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(holder.imageView)
                    holder.textView_title.text = friend?.name
                }
            })
            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져
            val commentMap = TreeMap<String, ChatModel.Comment>(reverseOrder())
            commentMap.putAll(chatModel[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.textView_lastMessage.text = chatModel[position].comments[lastMessageKey]?.message

            //채팅창 선책 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return chatModel.size
        }
    }
}
