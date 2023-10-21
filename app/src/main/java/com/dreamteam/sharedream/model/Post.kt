package com.dreamteam.sharedream.model

import android.net.Uri
import java.text.DateFormat

data class Post(
    val uid : String,
    val title : String,
    val price : String,
    val category : String,
    val address : String,
    val deadline : String,
    val desc : String,
    val img : List<String>,
)
