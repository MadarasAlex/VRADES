package com.example.vrades.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vrades.R
import com.example.vrades.databinding.FragmentResultsBinding
import com.example.vrades.databinding.FragmentSettingsBinding
import com.example.vrades.viewmodels.ResultsViewModel
import com.example.vrades.viewmodels.SettingsViewModel


class ResultsFragment : Fragment() {

    private lateinit var viewModel: ResultsViewModel
    private val _binding: FragmentResultsBinding? = null
    var binding = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.executePendingBindings()
        return binding.root
    }


}