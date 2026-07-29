package com.yourcompany.digitaltok.ui.device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.model.DeviceData
import com.yourcompany.digitaltok.data.repository.DeviceRepository
import kotlinx.coroutines.launch

/**
 * Device 관련 비즈니스 로직을 처리하고 UI 상태를 관리하는 ViewModel
 */
class DeviceViewModel : ViewModel() {

    private val repository = DeviceRepository()

    // 서버로부터의 장치 등록 결과를 관찰하기 위한 LiveData
    private val _registrationResult = MutableLiveData<Result<DeviceData>>()
    val registrationResult: LiveData<Result<DeviceData>> = _registrationResult

    // 특정 장치의 상세 정보 조회 결과를 관찰하기 위한 LiveData
    private val _deviceDetailsResult = MutableLiveData<Result<DeviceData>>()
    val deviceDetailsResult: LiveData<Result<DeviceData>> = _deviceDetailsResult

    // 장치 삭제 결과를 관찰하기 위한 LiveData
    private val _deletionResult = MutableLiveData<Result<DeviceData>>()
    val deletionResult: LiveData<Result<DeviceData>> = _deletionResult
    /**
     * NFC 태그 UID를 사용하여 서버에 장치 등록을 요청
     * 이 함수가 호출되면, viewModelScope에 의해 백그라운드에서 서버 통신이 실행되고,
     * 그 결과는 registrationResult LiveData에 저장됩니다.
     */
    fun registerDevice(nfcUid: String) {
        viewModelScope.launch {
            val result = repository.registerDevice(nfcUid)
            _registrationResult.postValue(result)
        }
    }
    /**
     * nfcUid를 사용하여 서버에 상세 정보 조회를 요청합니다.
     * 결과는 deviceDetailsResult LiveData에 저장됩니다.
     */
    fun getDeviceByNfcUid(nfcUid: String) {
        viewModelScope.launch {
            val result = repository.getDeviceByNfcUid(nfcUid)
            _deviceDetailsResult.postValue(result)
        }
    }
    /**
     * nfcUid를 사용하여 서버에서 장치를 삭제합니다.
     * 결과는 deletionResult LiveData에 저장됩니다.
     */
    fun deleteDevice(nfcUid: String) {
        viewModelScope.launch {
            val result = repository.deleteDevice(nfcUid)
            _deletionResult.postValue(result)
        }
    }
}
