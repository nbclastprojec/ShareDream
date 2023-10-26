package com.dreamteam.sharedream.model

import android.net.Uri

data class PostRcv(

    val uid: String,
    val title: String,
    val price: String,
    val category: String,
    val address: String,
    val deadline: String,
    val desc: String,
    var imgs: List<Uri>,
    val nickname: String,
    val likeUsers: List<String>,
    val token: String

)
