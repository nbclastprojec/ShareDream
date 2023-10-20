package com.dreamteam.sharedream.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.databinding.FragmentCategoryDialogBinding
import com.google.android.material.chip.Chip

class CategoryDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentCategoryDialogBinding

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
            categorySelectionListener?.onCategorySelected(selectedCategory)
            dismiss()
        }
    }

    fun setCategorySelectionListener(listener: CategorySelectionListener) {
        categorySelectionListener = listener
    }
}