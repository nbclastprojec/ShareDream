package com.dreamteam.sharedream.viewmodel.Inquiry

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.sharedream.model.InquiryData
import com.dreamteam.sharedream.viewmodel.Inquiry.InquiryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InquiryViewModel(private val inquiryRepository: InquiryRepository) : ViewModel() {



    fun uploadInquiry(inquiryData: InquiryData){
        viewModelScope.launch(Dispatchers.IO){
            try {
                inquiryRepository.uploadInquiry(inquiryData)
            }catch (e: Exception){
                Log.d("xxxx", "uploadInquiry Error : $e ")
            }
        }
    }
}