package com.dreamteam.sharedream.model

data class UserData (
    val id : String,
    var nickname : String,
    var intro : String,
    val email : String,
    val number : String,




)
{
    constructor() :this("","","","","")
}