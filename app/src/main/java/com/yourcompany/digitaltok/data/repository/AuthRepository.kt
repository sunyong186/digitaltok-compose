package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.*
import com.yourcompany.digitaltok.data.network.RetrofitClient

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun signup(email: String, password: String, phone: String): retrofit2.Response<ApiResponse<SignupResult>> {
        kotlinx.coroutines.delay(500)
        return retrofit2.Response.success(
            ApiResponse(
                isSuccess = true, code = "200", message = "Success",
                result = SignupResult(userId = 1, email = email, nickname = "MockUser", accessToken = "mock_access", refreshToken = "mock_refresh")
            )
        )
    }

    suspend fun login(email: String, password: String): retrofit2.Response<ApiResponse<LoginResult>> {
        kotlinx.coroutines.delay(500)
        return retrofit2.Response.success(
            ApiResponse(
                isSuccess = true, code = "200", message = "Success",
                result = LoginResult(grantType = "Bearer", accessToken = "mock_access", refreshToken = "mock_refresh", accessTokenExpiresIn = 3600L)
            )
        )
    }

    suspend fun duplicateCheck(email: String): retrofit2.Response<ApiResponse<String>> {
        kotlinx.coroutines.delay(300)
        return retrofit2.Response.success(
            ApiResponse(
                isSuccess = true, code = "200", message = "Success",
                result = "사용 가능한 이메일입니다."
            )
        )
    }

    suspend fun resetPassword(email: String): retrofit2.Response<ApiResponse<String>> {
        kotlinx.coroutines.delay(300)
        return retrofit2.Response.success(
            ApiResponse(
                isSuccess = true, code = "200", message = "Success",
                result = "비밀번호가 초기화되었습니다."
            )
        )
    }



}
