package com.dreamteam.sharedream

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.dreamteam.sharedream.databinding.FragmentPasswordCheckDialogBinding


class EmailPasswordDialogFragment : DialogFragment() {
    private var _binding: FragmentPasswordCheckDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPasswordCheckDialogBinding.inflate(inflater, container, false)

        binding.backEmail.setOnClickListener {
            dismiss()
        }
        
        binding.btnLogin.setOnClickListener {
            dismiss()
            val intent= Intent(requireContext(),LogInMainFragment::class.java)
            startActivity(intent)
            
            
        }

        return binding.root
    }




}