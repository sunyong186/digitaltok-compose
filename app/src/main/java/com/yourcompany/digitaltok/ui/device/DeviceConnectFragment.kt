package com.yourcompany.digitaltok.ui.device

import android.content.Context
import android.nfc.NfcManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.yourcompany.digitaltok.databinding.FragmentDeviceConnectBinding
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.MainUiViewModel

class DeviceConnectFragment : Fragment() {

    private var _binding: FragmentDeviceConnectBinding? = null
    private val binding get() = _binding!!

    //  연결 상태는 MainViewModel (단일 소스)
    private val mainViewModel: MainViewModel by activityViewModels()

    //  탭 이동 이벤트는 MainUiViewModel
    private val mainUiViewModel: MainUiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.connectTopAppBar.titleTextView.text = "기기연결"
        binding.connectTopAppBar.backButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.nfcConnectButton.setOnClickListener {
            checkNfcAndProceed()
        }
    }

    private fun checkNfcAndProceed() {
        val nfcManager = context?.getSystemService(Context.NFC_SERVICE) as? NfcManager
        val nfcAdapter = nfcManager?.defaultAdapter

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled) {
                NfcDisabledFragment().show(parentFragmentManager, "NfcDisabledDialog")
            } else {
                // "연결 시작" 누르면 -> 홈에서 '연결 안됨' 화면 보이게
                mainViewModel.setDeviceConnected(false)

                parentFragmentManager.beginTransaction()
                    .replace((requireView().parent as ViewGroup).id, DeviceSearchingFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
