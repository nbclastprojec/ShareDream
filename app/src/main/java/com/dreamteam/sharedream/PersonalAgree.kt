package com.dreamteam.sharedream

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.databinding.FragmentAgreeBinding
import com.dreamteam.sharedream.databinding.FragmentPersonalAgreeBinding


class PersonalAgree : DialogFragment() {
    private var _binding: FragmentPersonalAgreeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonalAgreeBinding.inflate(inflater, container, false)

        binding.agreeBtn.setOnClickListener {
            dismiss()
        }
        return binding.root
    }





}