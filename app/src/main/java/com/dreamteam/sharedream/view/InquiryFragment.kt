package com.dreamteam.sharedream.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.Util.ToastMsg
import com.dreamteam.sharedream.Util.Util
import com.dreamteam.sharedream.databinding.FragmentInquiryBinding
import com.dreamteam.sharedream.model.InquiryData
import com.dreamteam.sharedream.viewmodel.Inquiry.InquiryRepository
import com.dreamteam.sharedream.viewmodel.Inquiry.InquiryRepositoryImpl
import com.dreamteam.sharedream.viewmodel.Inquiry.InquiryViewModel
import com.dreamteam.sharedream.viewmodel.Inquiry.InquiryViewModelFactory
import com.google.firebase.Timestamp

class InquiryFragment : Fragment() {

    private var _binding: FragmentInquiryBinding? = null
    private val binding get() = _binding!!

    private lateinit var spinner: Spinner
    private lateinit var selectedCategory: String

    private val inquiryRepository: InquiryRepository = InquiryRepositoryImpl()
    private val inquiryViewModelFactory = InquiryViewModelFactory(inquiryRepository)
    private val inquiryViewModel: InquiryViewModel by viewModels { inquiryViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInquiryBinding.inflate(inflater, container, false)

        // 스피너 설정
        spinner = binding.inquirySpinner

        val items = resources.getStringArray(R.array.inquiry_category)

        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스피너 아이템 클릭 시 category 지정
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> selectedCategory = USAGE
                    1 -> selectedCategory = WITHDRAWAL
                    2 -> selectedCategory = ACCOUNT_ERROR
                    3 -> selectedCategory = POST_ERROR
                    4 -> selectedCategory = USER_REPORT
                    5 -> selectedCategory = SERVICE_PROPOSAL
                    6 -> selectedCategory = ANOTHER
                }
                Log.d("xxxx", "onItemSelected selectedCategory: $selectedCategory")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                false
            }
        }

        // 뒤로가기 버튼 클릭 이벤트
        binding.backButtonHelppage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 완료버튼 클릭 이벤트
        binding.helpSendButton.setOnClickListener {
            val inquiryTitle = binding.helpEtvTitle
            val inquiryEmail = binding.helpEtvEmailaddress
            val inquiryDesc = binding.helpEtvDesc
            if (inquiryTitle.text.isNotEmpty() || inquiryEmail.text.isNotEmpty() || inquiryDesc.text.isNotEmpty()) {

                val inquiryData = InquiryData(
                    Constants.currentUserUid!!,
                    selectedCategory,
                    inquiryEmail.text.toString(),
                    inquiryTitle.text.toString(),
                    inquiryDesc.text.toString(),
                    Timestamp.now()
                )

                // todo 회원탈퇴 처리?
                if (inquiryData.category == WITHDRAWAL) {
                    Util.showDialog(requireContext(),"회원 탈퇴","회원 탈퇴 시 기존 데이터를 복구할 수 없습니다."){inquiryViewModel.uploadInquiry(inquiryData)}
                } else {
                    inquiryViewModel.uploadInquiry(inquiryData)
                    parentFragmentManager.popBackStack()
                }
            }
            // 문의 페이지 입력란이 비었을 경우 토스트 메시지.
            else {
                ToastMsg.makeToast(requireContext(), "")
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val USAGE = "Inquiry_Usage"
        const val WITHDRAWAL = "Inquiry_MemberWithdrawal"
        const val ACCOUNT_ERROR = "Inquiry_AccountError"
        const val POST_ERROR = "Inquiry_PostError"
        const val USER_REPORT = "Inquiry_UserReport"
        const val SERVICE_PROPOSAL = "Inquiry_ServiceProposal"
        const val ANOTHER = "Inquiry_Another"
    }
}