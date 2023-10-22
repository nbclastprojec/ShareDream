package com.dreamteam.sharedream.model

data class Post(
    val uid : String,
    val title : String,
    val price : String,
    val category : String,
    val address : String,
    val deadline : String,
    val desc : String,
    val imgs : List<String>,
)
