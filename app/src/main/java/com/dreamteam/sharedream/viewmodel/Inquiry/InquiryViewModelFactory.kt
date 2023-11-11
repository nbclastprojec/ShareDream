package com.dreamteam.sharedream.viewmodel.Inquiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class InquiryViewModelFactory(private val inquiryRepository: InquiryRepository)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InquiryViewModel::class.java)){
            return InquiryViewModel(inquiryRepository) as T
        }
        throw IllegalArgumentException("Unknown InquiryViewModel")
    }
}