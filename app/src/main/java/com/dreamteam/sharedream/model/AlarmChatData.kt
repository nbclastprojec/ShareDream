package com.dreamteam.sharedream.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class AlarmChatData(val uid: String,
                         val title: String,
                         var imgs: List<String>,
                         val nickname: String,
                         val timestamp: Timestamp,
                         val documentId: String,
                         val myDocuId : String
) : Serializable {

    constructor() : this(
        "", "", listOf(), "", Timestamp.now(),"",""
    )
}
