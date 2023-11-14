package com.dreamteam.sharedream.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class AlarmChatData(
    var name: String? = null,
    val profileImageUrl: String? = null,
    val uid: String? = null,
    val myDocuId: String? = null,
    val timestamp: Timestamp
)