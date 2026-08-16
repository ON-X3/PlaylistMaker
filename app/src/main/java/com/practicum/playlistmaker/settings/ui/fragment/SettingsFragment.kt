package com.practicum.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeDarkTheme().observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
        }

        binding.themeSwitcher.setOnClickListener {
            viewModel.switchTheme(binding.themeSwitcher.isChecked)
        }

        binding.shareButton.setOnClickListener {
            viewModel.onShareAppClick()
        }

        binding.supportButton.setOnClickListener {
            viewModel.onSupportClick()
        }

        binding.agreementButton.setOnClickListener {
            viewModel.onTermsClick()
        }

        viewModel.onCreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}