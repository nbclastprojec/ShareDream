package com.dreamteam.sharedream.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.databinding.FragmentCategoryDialogBinding
import com.google.android.material.chip.Chip

@Suppress("DEPRECATION")
class CategoryDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentCategoryDialogBinding

    //카테고리 선택 이벤트 리스닝
    interface CategorySelectionListener {
        fun onCategorySelected(category: String)
    }

    private var categorySelectionListener: CategorySelectionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chipgroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedChip = view.findViewById<Chip>(checkedId)
            val selectedCategory = selectedChip.text.toString()
            //selected된 값 전달
            categorySelectionListener?.onCategorySelected(selectedCategory)
            Log.d("nyh", "onViewCreated: $selectedCategory")
            dismiss()
        }
    }

    //home으로 기능 내보내기
    fun setCategorySelectionListener(listener: CategorySelectionListener) {
        categorySelectionListener = listener
        Log.d("nyh", "setCategorySelectionListener: $listener")
    }
}