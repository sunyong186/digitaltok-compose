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


    suspend fun registerDevice(nfcUid: String): Result<DeviceData> = try {
        val response = apiService.registerDevice(
            DeviceRegistrationRequest(nfcUid = nfcUid)
        )

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Log.d("DeviceRepository", "기기 연결 성공 ${body.result}")
                Result.success(body.result)
            } else {
                val errorMessage = body?.message ?: "Response body, isSuccess, or result is problematic"
                Log.e("DeviceRepository", "Device registration failed: $errorMessage")
                Result.failure(RuntimeException(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Log.e("DeviceRepository", "Device registration HTTP error: ${response.code()} - $errorBody")
            Result.failure(RuntimeException("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Log.e("DeviceRepository", "Network or other exception: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun getDeviceByNfcUid(nfcUid: String): Result<DeviceData> = try {
        val response = apiService.getDeviceByNfcUid(nfcUid)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Log.d("DeviceRepository", "기기 상태 조회 성공 ${body.result}")
                Result.success(body.result)
            } else {
                val errorMessage = body?.message ?: "Response body, isSuccess, or result is problematic"
                Log.e("DeviceRepository", "Get device by NFC UID failed: $errorMessage")
                Result.failure(RuntimeException(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Log.e("DeviceRepository", "Get device by NFC UID HTTP error: ${response.code()} - $errorBody")
            Result.failure(RuntimeException("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Log.e("DeviceRepository", "Network or other exception: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun deleteDevice(nfcUid: String): Result<DeviceData> = try {
        val response = apiService.deleteDeviceByNfcUid(nfcUid)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Log.d("DeviceRepository", "기기 연결 해제 성공 ${body.result}")
                Result.success(body.result)
            } else {
                val errorMessage = body?.message ?: "Response body, isSuccess, or result is problematic"
                Log.e("DeviceRepository", "Device deletion failed: $errorMessage")
                Result.failure(RuntimeException(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Log.e("DeviceRepository", "Device deletion HTTP error: ${response.code()} - $errorBody")
            Result.failure(RuntimeException("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Log.e("DeviceRepository", "Network or other exception: ${e.message}", e)
        Result.failure(e)
    }
}
