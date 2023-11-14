package com.dreamteam.sharedream.chat

import com.google.firebase.Timestamp

data class Chatting(
    var name : String? = null,
    val profileImageUrl : String? = null,
    val uid : String? = null,
//    val myDocuId : String? = null,
//    val timestamp: Timestamp
)
