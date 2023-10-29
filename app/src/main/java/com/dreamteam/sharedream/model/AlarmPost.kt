package com.dreamteam.sharedream.model

data class AlarmPost(
    val uid: String,
    val title: String,
    val price: String,
    val category: String,
    val address: String,
    val deadline: String,
    val desc: String,
    var imgs: List<String>,
    val nickname: String,
    val likeUsers: List<String>,
    val token: String,
    val documentId:String

) {
    constructor() : this("", "", "", "", "", "", "", listOf(), "", listOf(), "","")
}
