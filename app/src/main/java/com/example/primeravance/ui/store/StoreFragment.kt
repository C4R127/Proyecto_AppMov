package com.example.primeravance.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.primeravance.databinding.FragmentStoreBinding

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.categoryFruits.setOnClickListener {
            Toast.makeText(requireContext(), "Fresh Fruits & Vegetable selected", Toast.LENGTH_SHORT).show()
        }

        binding.categoryMeat.setOnClickListener {
            Toast.makeText(requireContext(), "Meat & Fish selected", Toast.LENGTH_SHORT).show()
        }

        binding.categoryDairy.setOnClickListener {
            Toast.makeText(requireContext(), "Dairy & Eggs selected", Toast.LENGTH_SHORT).show()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Toast.makeText(requireContext(), "Search activated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
