package com.dreamteam.sharedream.viewmodel.Inquiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class InquiryViewModelFactory(private val inquiryRepository: InquiryRepository)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InquiryViewModel::class.java) ->  InquiryViewModel(inquiryRepository) as T

            else -> throw IllegalArgumentException("Unknown InquiryViewModel")
        }
    }
}