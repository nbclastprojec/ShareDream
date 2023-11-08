package com.dreamteam.sharedream.home


import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dreamteam.sharedream.databinding.FragmentCategoryDialogBinding
import com.google.android.material.chip.Chip

@Suppress("DEPRECATION")
class CategoryDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentCategoryDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: CategoryViewModel by activityViewModels()

        binding.chipgroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedChip = view.findViewById<Chip>(checkedId)
            val selectedCategories = mutableListOf<String>()
            if (selectedChip != null) {
                val selectedCategory = selectedChip.text.toString()
                if (selectedCategories.contains(selectedCategory)) {

                    selectedCategories.remove(selectedCategory)
                } else {
                    selectedCategories.add(selectedCategory)
                }
                viewModel.onCategorySelected(selectedCategory)
                Log.d("nyh", "Category Dialog Frag onViewCreated: $selectedCategory")
            }
        }

        binding.apply {
            max10000.setOnClickListener {
                val minPrice: Long = 0
                val maxPrice: Long = 10000
                viewModel.onPriceSelected(minPrice, maxPrice)
            }
            max30000.setOnClickListener {
                val minPrice: Long = 10000
                val maxPrice: Long = 30000
                viewModel.onPriceSelected(minPrice, maxPrice)
            }
            max50000.setOnClickListener {
                val minPrice: Long = 30000
                val maxPrice: Long = 50000
                viewModel.onPriceSelected(minPrice, maxPrice)
            }
            max100000.setOnClickListener {
                val minPrice: Long = 50000
                val maxPrice: Long = 100000
                viewModel.onPriceSelected(minPrice, maxPrice)
            }
            max150000.setOnClickListener {
                val minPrice: Long = 100000
                val maxPrice: Long = 150000
                viewModel.onPriceSelected(minPrice, maxPrice)
            }
            maxfull.setOnClickListener {
                val minPrice: Long = 30000
                val maxPrice: Long = 99999999999
                viewModel.onPriceSelected(minPrice, maxPrice)
            }
        }
        binding.button.setOnClickListener {
            dismiss()
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.show()
        dialog.window?.attributes = layoutParams

        return dialog
    }
}