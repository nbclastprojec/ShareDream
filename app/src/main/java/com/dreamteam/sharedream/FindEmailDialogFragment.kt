package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.databinding.FragmentFindEmailDialogBinding


class FindEmailDialogFragment : DialogFragment() {
    private var _binding: FragmentFindEmailDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindEmailDialogBinding.inflate(inflater, container, false)

        val userId = arguments?.getString("userId")
        if (!userId.isNullOrEmpty()) {
            setData(userId)
        }

        binding.findPasswordBtn.setOnClickListener {
            dismiss()
            val SendPasswordFragment = SendPasswordFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SendPasswordFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.btnLogin.setOnClickListener {
            dismiss()
            val intent = Intent(requireContext(), LogInMainFragment::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    fun setData(userId: String?) {
        if (userId != null) {
            binding.showId.text = "아이디는 $userId 입니다!"
        } else {
            binding.showId.text = "아이디 정보를 찾을 수 없습니다."
        }
    }
}