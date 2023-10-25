package com.dreamteam.sharedream.model

data class Post(
    val uid : String,
    val title : String,
    val price : String,
    val category : String,
    val address : String,
    val deadline : String,
    val desc : String,
    var imgs : List<String>,
    val nickname : String,
    val likeUsers : List<String>,

)
{
    constructor() : this("","", "", "", "", "","", listOf(),"", listOf())
}
