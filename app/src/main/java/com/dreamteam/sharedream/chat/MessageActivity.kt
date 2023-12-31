package com.dreamteam.sharedream.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.FcmRetrofitInstance
import com.dreamteam.sharedream.databinding.ActivityChatBinding
import com.dreamteam.sharedream.databinding.ChatDialogBinding
import com.dreamteam.sharedream.databinding.ChatItemBinding
import com.dreamteam.sharedream.home.HomeRepository
import com.dreamteam.sharedream.model.NotificationBody
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class MessageActivity : AppCompatActivity() {

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomuid: String? = null
    private var destinationUid: String? = null
    private var uid: String? = null
    private var recyclerView: RecyclerView? = null
    private val storage = Firebase.storage
    private lateinit var binding: ActivityChatBinding
    private var document: String? = null
    private var myCustomDialog: MyCustomDialog? = null
    private val postFeedViewModel: MyPostFeedViewModel by viewModels()
    private val db = Firebase.firestore
    private val PICK_IMAGE_REQUEST = 1
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root

        val imageView = binding.chatSendBtn
        val editText = binding.chattext

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월 dd일 hh:mm")
        val realTime = dateFormat.format(Date(time)).toString()
        val backbtn = binding.backButtonChat

        val plusButton = binding.plubtn
        val imageButton = binding.imageIc
        val otherlistLayout = binding.otherListLayout
        val backButtonOtherList = binding.backbuttonOtherlist
        val backButtonPlus = binding.backButtonPlus
        val plusLayout = binding.plusLayout

        val receivedDocumentId = intent.getStringExtra("documnetUid").toString()

        val store = FirebaseFirestore.getInstance()

        document = receivedDocumentId

        val userData = store.collection("Posts").document(receivedDocumentId)
        userData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("nickname") // 닉네임
                    val postUseruid = document.getString("uid") // uid

                    binding.chat.text = nickname
                    destinationUid = postUseruid


                } else {
                    Log.d("MessageActivity", "문서가 없는 예전글이에요.")
                }
            }



        setContentView(view)

        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = binding.chatRecycleView

        imageView.setOnClickListener {

            val messageText = editText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                Log.d("susu", "$destinationUid")
                val chatModel = ChatModel()
                chatModel.users.put(uid.toString(), true)
                chatModel.users.put(destinationUid!!, true)
                val comment = ChatModel.Comment(uid, editText.text.toString(), realTime)
                if (chatRoomuid == null) {
                    imageView.isEnabled = false
                    Handler().postDelayed({
                        imageView.isEnabled = true // 1초 후 버튼 재활성화
                    }, 1000L)
                    fireDatabase.child("ChatRoom").push().setValue(chatModel).addOnSuccessListener {
                        checkChatRoom()
                        Handler().postDelayed({
                            fireDatabase.child("ChatRoom").child(chatRoomuid.toString())
                                .child("comments").push().setValue(comment)
                            editText.text = null
                        }, 1000L)
                        getTokenFromUser()
                    }
                } else {
                    imageView.isEnabled = false
                    Handler().postDelayed({
                        imageView.isEnabled = true // 1초 후 버튼 재활성화
                    }, 1000L)
                    fireDatabase.child("ChatRoom").child(chatRoomuid.toString()).child("comments")
                        .push().setValue(comment)
                    editText.text = null
                    getTokenFromUser()
                }
            } else {
                Toast.makeText(this, "메세지를 입력하세요.", Toast.LENGTH_LONG).show()
            }
        }

        backbtn.setOnClickListener {
            super.onBackPressed()
        }
        checkChatRoom()

        val listBtn = binding.listbtn

        listBtn.setOnClickListener {
            val myCustomDialog = MyCustomDialog(this, object : CustomDialogInterface {
                override fun onDeleteBtnClicked() {
                    deleteChatRoom()
                    roomOut()
                    myCustomDialog?.dismiss()
                }

                override fun onCancelBtnClicked() {
                    myCustomDialog?.dismiss()
                }
            })
            myCustomDialog.show()
        }


        plusButton.setOnClickListener {
            plusLayout.visibility = View.VISIBLE
        }

        backButtonOtherList.setOnClickListener {
            otherlistLayout.visibility = View.GONE
            imageButton.visibility = View.VISIBLE
            backButtonOtherList.visibility = View.GONE
            backButtonPlus.visibility = View.VISIBLE
        }
        backButtonPlus.setOnClickListener {
            plusLayout.visibility = View.GONE
        }
        imageButton.setOnClickListener {
            externalImageAccess()
        }

    }

    private fun externalImageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 13 이상일 경우 if문 실행
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                )
            } else {
                openGallery()
            }
        } else {
            openGallery()
        }
    }

    private fun checkChatRoom() {
        fireDatabase.child("ChatRoom").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        val chatModel = item.getValue<ChatModel>()
                        if (chatModel?.users!!.containsKey(destinationUid)) {
                            chatRoomuid = item.key
                            recyclerView?.layoutManager = LinearLayoutManager(this@MessageActivity)
                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }

    private fun deleteChatRoom() {
        if (chatRoomuid != null) {
            fireDatabase.child("ChatRoom").child(chatRoomuid!!).removeValue().addOnSuccessListener {
                Toast.makeText(this, "채팅방이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                chatRoomuid = null
                recyclerView?.adapter?.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                Log.e("MessageActivity", "채팅방 삭제 실패: ${e.message}")
            }
        }
    }

    private fun roomOut() {
        onBackPressed()
    }

    inner class RecyclerViewAdapter :
        RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<ChatModel.Comment>()
        private var chat: Chatting? = null

        init {
            fireDatabase.child("users").child(destinationUid.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        chat = snapshot.getValue<Chatting>()
                        chat?.name = binding.chat.toString()
                        getMessageList()
                    }
                })
        }

        fun getMessageList() {
            fireDatabase.child("ChatRoom").child(chatRoomuid.toString()).child("comments")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        comments.clear()
                        for (data in snapshot.children) {
                            val item = data.getValue<ChatModel.Comment>()
                            comments.add(item!!)
                        }
                        notifyDataSetChanged()
                        // 메세지를 보낼 시 화면을 맨 밑으로 내림
                        recyclerView?.scrollToPosition(comments.size - 1)
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val itemBinding =
                ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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
            val imageMessages: ImageView = itemBinding.imageMessage

            fun bind(comment: ChatModel.Comment) {
                with(itemBinding) {
                    message.text = comment.message
                    time.text = comment.time
                    if (comment.uid == uid) {
                        if (comment.imageUrl?.isNotEmpty() == true) {
                            message.visibility = View.GONE
                            Glide.with(itemView.context)
                                .load(comment.imageUrl)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                                .into(imageMessages)
                            imageMessages.visibility = View.VISIBLE
                            message.setBackgroundResource(R.drawable.chatmine)
                            layoutMain.gravity = Gravity.RIGHT
                            time.gravity= Gravity.RIGHT
                            profile.visibility = View.INVISIBLE
                            name.visibility = View.INVISIBLE
                        } else {
                            imageMessages.visibility = View.GONE
                            profile.visibility = View.INVISIBLE
                            time.gravity= Gravity.RIGHT
                            name.visibility = View.INVISIBLE
                            message.setBackgroundResource(R.drawable.chatmine)
                            layoutMain.gravity = Gravity.RIGHT
                        }


                    } else {
                        val storageReference =
                            destinationUid?.let { storage.reference.child("ProfileImg").child(it) }
                        storageReference?.downloadUrl?.addOnSuccessListener { uri ->
                            Glide.with(itemView.context)
                                .load(uri)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                                .into(profile)
                        }?.addOnFailureListener { exception ->
                            Log.e("MessageActivity", "이미지 다운로드 실패: ${exception.message}")
                        }

                        if (comment.imageUrl?.isNotEmpty() == true) {
                            message.visibility = View.GONE
                            Glide.with(itemView.context)
                                .load(comment.imageUrl)
                                .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                                .into(imageMessages)
                            imageMessages.visibility = View.VISIBLE
                            message.setBackgroundResource(R.drawable.chat_other)
                            name.text = chat?.name
                            time.gravity= Gravity.LEFT
                            destination.visibility = View.VISIBLE
                            name.visibility = View.VISIBLE
                            layoutMain.gravity = Gravity.LEFT
                        } else {
                            imageMessages.visibility = View.GONE
                            message.setBackgroundResource(R.drawable.chat_other)
                            name.text = chat?.name
                            time.gravity= Gravity.LEFT
                            destination.visibility = View.VISIBLE
                            name.visibility = View.VISIBLE
                            layoutMain.gravity = Gravity.LEFT
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }

    interface CustomDialogInterface {
        fun onDeleteBtnClicked()
        fun onCancelBtnClicked()
    }

    inner class MyCustomDialog(
        context: Context,
        private val customDialogInterface: CustomDialogInterface
    ) : Dialog(context) {

        private var Binding: ChatDialogBinding? = null
        private val binding get() = Binding!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Binding = ChatDialogBinding.inflate(layoutInflater)
            setContentView(binding.root)

            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.dialogTitleTv.text = "채팅방 나가기"
            binding.dialogDescTv.text = "채팅방을 나가시겠습니까?"
            binding.dialogDescTv2.text = "채팅방을 나가면 대화내용이 모두 삭제되고 \n 채팅 목록이 삭제됩니다."

            binding.dialogCancelBtn.setOnClickListener {
                customDialogInterface.onCancelBtnClicked()
            }
            binding.dialogBtn.setOnClickListener {
                customDialogInterface.onDeleteBtnClicked()
            }
        }
    }

    private fun getTokenFromUser() {

        val postRef = destinationUid?.let { db.collection("UserData").document(it) }

        if (postRef != null) {
            postRef
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {

                        val token = documentSnapshot.getString("token")
                        if (token != null) {
                            val userId = Constants.currentUserInfo?.nickname
                            val notificationTitle = "채팅알림"
                            val notificationBody = "${userId}님이 채팅을 보냈어요!"
                            Log.d("nyh", " userId: $userId")
                            Log.d("nyh", " token: $token")
                            //
                            val data = NotificationBody.NotificationData(
                                notificationTitle!!, notificationBody, userId!!
                            )
                            val body = NotificationBody(token, data)
                            Log.d("nyh", "getTokenFromPost: send value of body $body")
                            postFeedViewModel.sendNotification(body)

                            storage.reference.child("ProfileImg").child("$uid").downloadUrl
                                .addOnSuccessListener { image ->
                                    val profile = image

                                    val profileImageUrl = profile
                                    Log.d(
                                        "nyh","getTokenFromUser profileImageUrl: $profileImageUrl"
                                    )
                                    Log.d("nyh", "getTokenFromUser profile: $profile")
                                    val notiLIst = hashMapOf(
                                        "title" to notificationTitle,
                                        "body" to notificationBody,
                                        "nickname" to userId,
                                        "uid" to destinationUid,
                                        "time" to Timestamp.now(),
                                        "profileImageUrl" to profileImageUrl
                                    )
                                    db.collection("notifyChatList")
                                        .add(notiLIst)
                                        .addOnSuccessListener { task ->
                                            val myDocuId = task.id
                                            val updatedData = mapOf("myDocuId" to myDocuId)
                                            Log.d("nyh", "getTokenFromPost:docuId $myDocuId")
                                            db.collection("notifyChatList")
                                                .document(myDocuId)
                                                .update(updatedData)
                                                .addOnSuccessListener {
                                                }
                                            Log.d("nyh", "getTokenFromPost: $task")
                                        }
                                    Log.d("nyh", "getTokenFromPost: token = $token")
                                    Log.d("nyh", "getTokenFromPost: suc title =$notificationTitle")

                                }
                        }
                    } else {
                        Log.d("nyh", "getTokenFromPost: elsefail")
                    }
                }.addOnFailureListener {
                    Log.d("nyh", "getTokenFromPost: failurfail")
                }
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                sendMessageWithImage(imageUri)
            }
        }
    }



    private fun sendMessageWithImage(imageUri: Uri) {
        val storageReference =
            storage.reference.child("ChatImages").child("${System.currentTimeMillis()}.jpg")

        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // 이미지 업로드 성공 시 처리
                storageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    val time = System.currentTimeMillis()
                    val dateFormat = SimpleDateFormat("MM월 dd일 hh:mm")
                    val realTime = dateFormat.format(Date(time)).toString()

                    val comment = ChatModel.Comment(uid, "", realTime, imageUrl.toString())

                    // Firebase에 채팅 메시지 저장
                    fireDatabase.child("ChatRoom").child(chatRoomuid.toString()).child("comments")
                        .push().setValue(comment)
                }
            }
            .addOnFailureListener { exception ->
                // 이미지 업로드 실패 시 처리
                Log.e("MessageActivity", "이미지 업로드 실패: ${exception.message}")
            }
    }
}


