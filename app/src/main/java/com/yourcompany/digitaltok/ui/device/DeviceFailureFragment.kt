package com.yourcompany.digitaltok.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yourcompany.digitaltok.databinding.FragmentDeviceFailureBinding
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.faq.HelpFragment

class DeviceFailureFragment : Fragment() {

    private var _binding: FragmentDeviceFailureBinding? = null
    private val binding get() = _binding!!

    // 실패 진입 시 연결 안됨 확정 (단일 소스)
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceFailureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.setDeviceConnected(false)

        binding.failureTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceSearchingFragment())
                .commit()
        }

        binding.restartButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceSearchingFragment())
                .commit()
        }

        binding.helpButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, HelpFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
