package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.ApiResponse
import com.yourcompany.digitaltok.data.model.EmailChangeRequest
import com.yourcompany.digitaltok.data.model.LogoutRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.PATCH

interface AccountApiService {

    @HTTP(method = "DELETE", path = "auth/logout", hasBody = true)
    suspend fun logout(@Body request: LogoutRequest): ApiResponse<String>

    @DELETE("users/me")
    suspend fun withdraw(): ApiResponse<String>

    @PATCH("users/email")
    suspend fun changeEmail(@Body request: EmailChangeRequest): ApiResponse<String>
}
