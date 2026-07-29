package com.yourcompany.digitaltok.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yourcompany.digitaltok.databinding.FragmentDeviceSuccessBinding
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel

class DeviceSuccessFragment : Fragment() {

    private var _binding: FragmentDeviceSuccessBinding? = null
    private val binding get() = _binding!!

    // 탭 이동 이벤트
    private val mainUiViewModel: MainUiViewModel by activityViewModels()

    // 연결 상태 단일 소스
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 성공 진입 = 연결됨 확정 (단일 소스)
        mainViewModel.setDeviceConnected(true)

        binding.successTopAppBar.backButton.setOnClickListener {
            mainUiViewModel.requestNavigate("home")
        }

        binding.decoButton.setOnClickListener {
            mainUiViewModel.requestNavigate("decorate")
        }

        binding.homeButton.setOnClickListener {
            mainUiViewModel.requestNavigate("home")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
