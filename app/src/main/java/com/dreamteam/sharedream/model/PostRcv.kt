package com.dreamteam.sharedream.model

import android.net.Uri
import com.google.firebase.Timestamp
import java.io.Serializable

data class PostRcv(
    val uid: String,
    val title: String,
    val price: Long,
    val category: String,
    val address: String,
    val deadline: String,
    val desc: String,
    var imgs: List<Uri>,
    val nickname: String,
    var likeUsers: List<String>,
    val token: String,
    val timestamp: Timestamp,
    val state : String,
    val documentId:String,
    val locationLatLng : List<Double>,
    val locationKeyword : List<String>,
    val endDate:String,

) : Serializable {
    constructor() : this(
        "",
        "",
        0,
        "",
        "",
        "",
        "",
        listOf(),
        "",
        listOf(),
        "",
        Timestamp.now(),
        "",
        "",
        listOf(),
        listOf(),
        ""
    )


}
