package com.dreamteam.sharedream

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dreamteam.sharedream.databinding.FragmentDetailpageBinding
import com.dreamteam.sharedream.databinding.FragmentHomeBinding
import com.dreamteam.sharedream.home.HomeFragment

class DetailPageFragment:Fragment(){
    private lateinit var binding:FragmentDetailpageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentDetailpageBinding.inflate(inflater,container,false)
        val value = arguments?.getString("SendDetail")
       Log.d("document123","sendValue:$value")

        return binding.root
    }

}