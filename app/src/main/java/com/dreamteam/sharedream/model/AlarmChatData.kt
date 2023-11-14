package com.dreamteam.sharedream.model

import android.net.Uri
import com.google.firebase.Timestamp
import java.io.Serializable

data class AlarmChatData(
    var title: String? = null,
    var nickname: String? = null,
    val profileImageUrl: String? = null,
    val uid: String? = null,
    val myDocuId: String? = null,
    val timestamp: Timestamp
)
{
    constructor() : this(
        "","", "", "", "", Timestamp.now()
    )
}
