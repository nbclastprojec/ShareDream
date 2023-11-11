package com.dreamteam.sharedream.viewmodel.Inquiry

import com.dreamteam.sharedream.model.InquiryData
import com.dreamteam.sharedream.viewmodel.Inquiry.InquiryRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InquiryRepositoryImpl: InquiryRepository {

    private val db = Firebase.firestore

    override fun uploadInquiry(inquiry: InquiryData) {
        db.collection(inquiry.category).document(inquiry.timestamp.toString()).set(inquiry)
    }
}