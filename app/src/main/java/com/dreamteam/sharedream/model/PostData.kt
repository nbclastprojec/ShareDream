package com.dreamteam.sharedream.model

data class PostData(
    val title: String,
    val value: Int,
    val category: String,
    val during: String,
    val mainText: String,
    val image: String
) {
    constructor() : this("", 0, "", "", "","")
}