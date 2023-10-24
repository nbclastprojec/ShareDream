package com.dreamteam.sharedream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dreamteam.sharedream.databinding.FragmentMyPostFeedDetailBinding
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel

class MyPostFeedDetailFragment : Fragment() {
    private var _binding: FragmentMyPostFeedDetailBinding? = null
    private val binding get() = _binding!!

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPostFeedDetailBinding.inflate(inflater, container, false)

        myPostFeedViewModel.currentPost.observe(viewLifecycleOwner) {
            binding.detailId.text = it.id
            binding.detailAddress.text = it.title
            binding.detailpageName.text = it.title
            binding.detailpageCategory.text = it.category
            binding.detailpageExplain.text = it.mainText
            binding.detailMoney.text = "${it.value} 원"
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailCancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}