package com.smogunovandrey.tasksplanning.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.smogunovandrey.tasksplanning.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment() {
    private val binding: FragmentSettingsBinding by lazy{
        FragmentSettingsBinding.inflate(layoutInflater)
    }

    private lateinit var model: SettingsObservableModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = SettingsObservableModel()
        binding.viewModel = model
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}