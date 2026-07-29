package com.yourcompany.digitaltok.ui.device

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.yourcompany.digitaltok.databinding.FragmentDeviceSearchingBinding
import com.yourcompany.digitaltok.ui.MainViewModel

class DeviceSearchingFragment : Fragment() {

    private var _binding: FragmentDeviceSearchingBinding? = null
    private val binding get() = _binding!!

    // 서버 통신을 위한 ViewModel
    private val deviceViewModel: DeviceViewModel by viewModels()

    // MainActivity와 NFC 태그 정보를 공유하기 위한 ViewModel
    private val nfcViewModel: NfcViewModel by activityViewModels()

    // 연결 상태 단일 소스
    private val mainViewModel: MainViewModel by activityViewModels()

    // NFC 관련 객체
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    // 태그 감지 시 UID를 임시 저장할 변수
    private var detectedNfcUid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceSearchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNfc()
        setupViews()
        observeViewModels()
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    private fun setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        val intent = Intent(requireContext(), requireActivity().javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    private fun setupViews() {
        binding.searchingTopAppbar.titleTextView.text = "기기 연결"
        binding.searchingTopAppbar.backButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun observeViewModels() {
        // 1. MainActivity가 전달한 NFC 태그 관찰
        nfcViewModel.tag.observe(viewLifecycleOwner) { tag ->
            if (tag != null) {
                val uid = bytesToHexString(tag.id)
                detectedNfcUid = uid
                Log.d("NFC", "Tag detected with UID: $uid. Checking if registered.")

                // 2. 기기 등록 여부 확인 요청
                deviceViewModel.getDeviceByNfcUid(uid)

                nfcViewModel.tagHandled()
            }
        }

        // 3. 기기 조회 결과 관찰
        deviceViewModel.deviceDetailsResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { deviceData ->
                Log.d("NFC", "Device already registered: $deviceData")
                navigateToSuccess()
            }.onFailure { error ->
                Log.e("NFC", "Get device failed: ${error.message}")
                if (error.message?.contains("404") == true) {
                    Log.d("NFC", "Device not found. Attempting to register.")
                    detectedNfcUid?.let { deviceViewModel.registerDevice(it) }
                } else {
                    navigateToFailure()
                }
            }
        }

        // 4. 기기 등록 결과 관찰
        deviceViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { deviceData ->
                Log.d("NFC", "Registration successful: $deviceData")
                navigateToSuccess()
            }.onFailure { error ->
                Log.e("NFC", "Registration failed: ${error.message}")

                if (
                    error.message?.contains("DEVICE400") == true ||
                    error.message?.contains("기기가 이미 연결되어 있습니다") == true
                ) {
                    Log.d("NFC", "Registration failed but device is already connected. Navigating to success.")
                    navigateToSuccess()
                } else {
                    navigateToFailure()
                }
            }
        }
    }

    private fun navigateToSuccess() {
        if (isAdded) {
            // 연결 성공 상태 업데이트(단일 소스)
            mainViewModel.setDeviceConnected(true)

            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceSuccessFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToFailure() {
        if (isAdded) {
            // 연결 실패 상태 업데이트(단일 소스)
            mainViewModel.setDeviceConnected(false)

            parentFragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, DeviceFailureFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "_" + String.format("%02X", it) }.drop(1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
