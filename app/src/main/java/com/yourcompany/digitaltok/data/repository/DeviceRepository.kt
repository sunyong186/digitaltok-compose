package com.yourcompany.digitaltok.data.repository

import android.util.Log
import com.yourcompany.digitaltok.data.model.DeviceData
import com.yourcompany.digitaltok.data.model.DeviceRegistrationRequest
import com.yourcompany.digitaltok.data.network.RetrofitClient

/**
 * 장치 관련 데이터 처리를 담당하는 클래스.
 */
class DeviceRepository {

    private val apiService = RetrofitClient.apiService


    suspend fun registerDevice(nfcUid: String): Result<DeviceData> {
        kotlinx.coroutines.delay(500)
        Log.d("DeviceRepository", "기기 등록 Mock 성공")
        return Result.success(DeviceData(deviceId = 1, registeredAt = "2024-01-01T00:00:00Z", unregisteredAt = null, status = "ACTIVE"))
    }

    suspend fun getDeviceByNfcUid(nfcUid: String): Result<DeviceData> {
        kotlinx.coroutines.delay(500)
        Log.d("DeviceRepository", "기기 상태 조회 Mock 성공")
        return Result.success(DeviceData(deviceId = 1, registeredAt = "2024-01-01T00:00:00Z", unregisteredAt = null, status = "ACTIVE"))
    }

    suspend fun deleteDevice(nfcUid: String): Result<DeviceData> {
        kotlinx.coroutines.delay(500)
        Log.d("DeviceRepository", "기기 연결 해제 Mock 성공")
        return Result.success(DeviceData(deviceId = 1, registeredAt = "2024-01-01T00:00:00Z", unregisteredAt = "2024-01-02T00:00:00Z", status = "INACTIVE"))
    }
}
