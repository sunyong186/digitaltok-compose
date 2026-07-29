package com.yourcompany.digitaltok.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class MainViewModel : ViewModel() {

    // --- Bottom Nav Visiblity ---
    private val _isBottomNavVisible = MutableLiveData<Boolean>(true)
    val isBottomNavVisible: LiveData<Boolean> = _isBottomNavVisible

    // --- Device Connection State ---
    private val _isDeviceConnected = MutableLiveData<Boolean>()
    val isDeviceConnected: LiveData<Boolean> = _isDeviceConnected

    private val _isDeviceConnecting = MutableLiveData<Boolean>()
    val isDeviceConnecting: LiveData<Boolean> = _isDeviceConnecting

    // --- Image Transfer State ---
    private val _isImageTransferring = MutableLiveData<Boolean>()
    val isImageTransferring: LiveData<Boolean> = _isImageTransferring

    // --- Last Transferred Image URL ---
    private val _lastTransferredImageUrl = MutableLiveData<String?>()
    val lastTransferredImageUrl: LiveData<String?> = _lastTransferredImageUrl


    init {
        // 초기 상태 설정
        _isDeviceConnected.value = false
        _isDeviceConnecting.value = false
        _isImageTransferring.value = false
        _isBottomNavVisible.value = true
        _lastTransferredImageUrl.value = null
    }

    fun setBottomNavVisibility(isVisible: Boolean) {
        _isBottomNavVisible.value = isVisible
    }

    fun setDeviceConnected(isConnected: Boolean) {
        _isDeviceConnected.value = isConnected
        if (isConnected) {
            _isDeviceConnecting.value = false // 연결 성공 시, 연결 시도 상태는 해제
        }
    }

    fun setDeviceConnecting(isConnecting: Boolean) {
        _isDeviceConnecting.value = isConnecting
    }

    fun setImageTransferring(isTransferring: Boolean) {
        _isImageTransferring.value = isTransferring
    }

    fun setLastTransferredImageUrl(url: String?) {
        _lastTransferredImageUrl.value = url
    }
}
