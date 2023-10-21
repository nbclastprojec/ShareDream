package com.dreamteam.sharedream.model

import java.sql.Timestamp

data class PostData(
    val title: String,
    val value: Int,
    val category: String,
    val during: String,
    val mainText: String,
    var image: String,
    val date:Timestamp
) {
    constructor() : this("", 0, "", "", "","",Timestamp(System.currentTimeMillis()))
}