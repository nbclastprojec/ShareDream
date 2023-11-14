package com.dreamteam.sharedream.viewmodel.Inquiry

import com.dreamteam.sharedream.model.InquiryData
import com.google.android.gms.tasks.Task

interface InquiryRepository {
    fun uploadInquiry(inquiry: InquiryData)
}