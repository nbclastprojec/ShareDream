package com.dreamteam.sharedream.model

data class NotificationBody(
    val to:String,
    val data: NotificationData
) {
    data class NotificationData(
        val title: String,
        val message : String
    )
}
