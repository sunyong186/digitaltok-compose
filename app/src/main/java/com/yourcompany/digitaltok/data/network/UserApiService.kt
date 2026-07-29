package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.ApiResponse
import com.yourcompany.digitaltok.data.model.UserMeResult
import retrofit2.http.GET

interface UserApiService {

    @GET("users/me")
    suspend fun getMyProfile(): ApiResponse<UserMeResult>
}
