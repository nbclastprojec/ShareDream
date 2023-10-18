package com.dreamteam.sharedream.home


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.databinding.FragmentHomeBinding
import com.dreamteam.sharedream.home.Edit.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mContext: Context
    private lateinit var homeAdapter: HomeAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onResume() {
        super.onResume()
        homeAdapter.postDataFromFirestore()
        Log.d("HomeFrag onResume", "nyh backbtnsuc??")

        // LiveData를 관찰하여 RecyclerView를 다시 로드합니다.
        viewModel.refreshData.observe(viewLifecycleOwner) { refresh ->
            if (refresh) {
                // RecyclerView를 갱신하는 작업을 여기에서 수행
                homeAdapter.postDataFromFirestore()

                // 갱신이 완료되면 ViewModel에서 값을 초기화합니다.
                viewModel.onRefreshComplete()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Homefrag", "nyh backbtnsuc??")

        mContext = requireContext()
        homeAdapter = HomeAdapter(mContext)


        // LiveData를 관찰하여 RecyclerView를 다시 로드합니다.
        viewModel.refreshData.observe(viewLifecycleOwner) { refresh ->
            if (refresh) {
                // RecyclerView를 갱신하는 작업을 여기에서 수행
                homeAdapter.postDataFromFirestore()

                // 갱신이 완료되면 ViewModel에서 값을 초기화합니다.
                viewModel.onRefreshComplete()
            }
        }

        binding.homeRecycle.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.homeRecycle.adapter = homeAdapter

    }
}