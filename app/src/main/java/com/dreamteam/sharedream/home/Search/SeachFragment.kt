package com.dreamteam.sharedream.home.Search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.databinding.FragmentSeachBinding
import com.dreamteam.sharedream.model.Post
import com.dreamteam.sharedream.view.PostDetailFragment


@Suppress("UNREACHABLE_CODE")
class SeachFragment : Fragment() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchadapter: SeachAdapter
    private lateinit var binding: FragmentSeachBinding
    private lateinit var mContext: Context


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeachBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        val searchEdit = binding.editTextSearch


        searchadapter = SeachAdapter(mContext, object : SeachAdapter.OnItemClickListener {
            override fun onItemClick(post: Post) {
                searchViewModel.onPostClicked(post)
            }
        })
        binding.searchRecycler.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.searchRecycler.adapter = searchadapter

        searchViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchadapter.setData(results)
            searchadapter.notifyDataSetChanged()
        }
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                //검색어 입력 후 searchTitle 실행
                val query = p0.toString()
                if (query.isNotEmpty()) {
                    searchViewModel.performSearch(query)
                    Log.d("nyh", "afterTextChanged: $p0 // $query ")
                } else {
                    Log.d("nyh", "afterTextChanged else: $p0 // $query")
                }
            }
        })

        binding.btnSearch.setOnClickListener {
            val query = searchEdit.text.toString()
            searchViewModel.performSearch(query)
        }

        val sortSpinner = binding.sortSpinner
        val sortOptions = resources.getStringArray(R.array.sort_home)
        val sortAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = sortAdapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> searchViewModel.performSearch("")
                    1 -> searchViewModel.sortSearchHighPrice("")
                    2 -> searchViewModel.sortSearchLowPrice("")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        searchViewModel.selectedPost.observe(viewLifecycleOwner) { selectedPost ->
            if (selectedPost != null) {
                // PostDetailFragment로 이동
                val postDetailFragment = PostDetailFragment()

                // Post 객체를 Bundle에 추가하여 PostDetailFragment로 전달
                val args = Bundle()
                args.putSerializable("post", selectedPost)
                postDetailFragment.arguments = args
                Log.d("nyh", "onViewCreated: 전달하는 args $args")

                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.frag_edit, postDetailFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                // 선택된 Post를 초기화
                searchViewModel.resetSelectedPost()
            }
        }
    }
}
