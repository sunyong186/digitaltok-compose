package com.yourcompany.digitaltok.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainUiViewModel : ViewModel() {

    // 홈 화면에서 "연결됨/안됨" UI 분기 (Compose에서 관찰 가능)
    var isDeviceConnected by mutableStateOf(false)
        private set

    // Fragment -> Compose로 탭 이동 요청
    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo

    fun updateDeviceConnected(connected: Boolean) {
        isDeviceConnected = connected
    }

    // 연결 성공 처리
    fun onDeviceConnected() {
        isDeviceConnected = true
        _navigateTo.value = "home"
    }

    fun requestNavigate(route: String) {
        _navigateTo.value = route
    }

    fun consumeNavigate() {
        _navigateTo.value = null
    }
}
