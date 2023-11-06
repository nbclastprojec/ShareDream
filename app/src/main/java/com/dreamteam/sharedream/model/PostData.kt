package com.dreamteam.sharedream.model

data class PostData(
    val id:String,
    val title: String,
    val value: Int,
    val category: String,
    val during: String,
    val mainText: String,
    var image: String,
    var uploadDate : String,
    var token : String,
    val endTime:String
)
{
    constructor() : this("","", 0, "", "", "","","","","")
}