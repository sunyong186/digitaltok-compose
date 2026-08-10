package com.yourcompany.digitaltok.ui.device

import android.content.Context
import android.nfc.NfcManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel

enum class DeviceFlowState {
    Connect, Searching, Success, Failure
}

@Composable
fun DeviceScreen(mainViewModel: MainViewModel, mainUiViewModel: MainUiViewModel) {
    var flowState by remember { mutableStateOf(DeviceFlowState.Connect) }
    var showNfcDisabledDialog by remember { mutableStateOf(false) }
    var currentDetectedUid by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val deviceViewModel: DeviceViewModel = viewModel()
    val nfcViewModel: NfcViewModel = viewModel(context as ComponentActivity)
    
    val tag by nfcViewModel.tag.observeAsState()
    val deviceDetailsResult by deviceViewModel.deviceDetailsResult.observeAsState()
    val registrationResult by deviceViewModel.registrationResult.observeAsState()

    // 1. 실제 NFC 태그 인식 시
    LaunchedEffect(tag) {
        if (tag != null && flowState == DeviceFlowState.Searching) {
            val uid = tag!!.id.joinToString("") { "_" + String.format("%02X", it) }.drop(1)
            currentDetectedUid = uid
            deviceViewModel.getDeviceByNfcUid(uid)
            nfcViewModel.tagHandled()
        }
    }
    
    // 2. 기기 조회 결과 처리
    LaunchedEffect(deviceDetailsResult) {
        deviceDetailsResult?.let { result ->
            result.onSuccess {
                flowState = DeviceFlowState.Success
                mainViewModel.setDeviceConnected(true)
            }.onFailure { error ->
                if (error.message?.contains("404") == true && currentDetectedUid != null) {
                    // 미등록 기기일 경우 등록 시도
                    deviceViewModel.registerDevice(currentDetectedUid!!)
                } else {
                    // 서버 에러 등 조회 실패
                    flowState = DeviceFlowState.Failure
                    mainViewModel.setDeviceConnected(false)
                }
            }
            deviceViewModel.clearDeviceDetailsResult() // Consume the result
        }
    }
    
    // 3. 기기 등록 결과 처리
    LaunchedEffect(registrationResult) {
        registrationResult?.let { result ->
            result.onSuccess {
                flowState = DeviceFlowState.Success
                mainViewModel.setDeviceConnected(true)
            }.onFailure { error ->
                if (error.message?.contains("DEVICE400") == true || error.message?.contains("기기가 이미 연결되어 있습니다") == true) {
                    // 이미 연결된 기기인 경우 성공으로 간주
                    flowState = DeviceFlowState.Success
                    mainViewModel.setDeviceConnected(true)
                } else {
                    // 등록 실패
                    flowState = DeviceFlowState.Failure
                    mainViewModel.setDeviceConnected(false)
                }
            }
            deviceViewModel.clearRegistrationResult() // Consume the result
        }
    }

    if (showNfcDisabledDialog) {
        NfcDisabledDialog(
            onDismiss = { showNfcDisabledDialog = false }
        )
    }

    when (flowState) {
        DeviceFlowState.Connect -> {
            DeviceConnectContent(
                onBackClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() },
                onProceedClick = {
                    val nfcManager = context.getSystemService(Context.NFC_SERVICE) as? NfcManager
                    val nfcAdapter = nfcManager?.defaultAdapter
                    if (nfcAdapter != null && !nfcAdapter.isEnabled) {
                        showNfcDisabledDialog = true
                    } else {
                        mainViewModel.setDeviceConnected(false)
                        flowState = DeviceFlowState.Searching
                    }
                }
            )
        }
        DeviceFlowState.Searching -> {
            DeviceSearchingContent(
                onBackClick = { (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() },
                onMockTag = { dummyUid ->
                    currentDetectedUid = dummyUid
                    deviceViewModel.getDeviceByNfcUid(dummyUid)
                }
            )
        }
        DeviceFlowState.Success -> {
            DeviceSuccessContent(
                onNavigateToDecorate = { mainUiViewModel.requestNavigate("decorate") },
                onNavigateToHome = { mainUiViewModel.requestNavigate("home") }
            )
        }
        DeviceFlowState.Failure -> {
            DeviceFailureContent(
                onBackClick = { flowState = DeviceFlowState.Searching },
                onRetryClick = { flowState = DeviceFlowState.Searching },
                onNavigateToHelp = { mainUiViewModel.requestNavigate("settings") } // Help was moved to settings tab
            )
        }
    }
}
