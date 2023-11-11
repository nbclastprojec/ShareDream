package com.dreamteam.sharedream.model

data class InquiryData(
    val uid: String,
    val category: String,
    val email: String,
    val title: String,
    val desc: String,
    val timestamp: com.google.firebase.Timestamp,

    )