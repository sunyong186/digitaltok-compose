package com.yourcompany.digitaltok.data.repository

import android.content.Context
import android.util.Log
import com.yourcompany.digitaltok.data.model.UserMeResult
import com.yourcompany.digitaltok.data.network.RetrofitClient
import com.yourcompany.digitaltok.data.network.UserApiService

class UserRepository(
    private val context: Context
) {
    private val api: UserApiService = RetrofitClient.create(UserApiService::class.java)

    suspend fun getMyProfile(): Result<UserMeResult> {
        return try {
            val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            val accessToken = prefs.getString("accessToken", null)

            if (accessToken.isNullOrBlank()) {
                return Result.failure(IllegalStateException("accessToken is missing"))
            }

            // ApiResponse 래퍼로 받기
            val res = api.getMyProfile()

            if (res.isSuccess && res.result != null) {
                Result.success(res.result)
            } else {
                Result.failure(IllegalStateException(res.message ?: "Failed to load profile"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "getMyProfile error", e)
            Result.failure(e)
        }
    }
}
