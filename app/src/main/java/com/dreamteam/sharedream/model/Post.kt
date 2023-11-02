package com.dreamteam.sharedream.model

import com.google.firebase.Timestamp
import java.io.Serializable


data class Post(
    val uid: String,
    val title: String,
    val price: Long,
    val category: String,
    val address: String,
    val deadline: String,
    val desc: String,
    var imgs: List<String>,
    val nickname: String,
    val likeUsers: List<String>,
    val token: String,
    // todo 타임스탬프 추가, state 추가 - 마감일은 따로 쓸 데가 있을 것 같다
    val timestamp : Timestamp,
    val state : String,
    val documentId : String,
    val endTime:String

) : Serializable
{
    constructor() : this("","", 0, "", "", "","", listOf(),"", listOf(),"",Timestamp.now(),"","","")
}
