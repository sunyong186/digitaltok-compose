package com.yourcompany.digitaltok.data.repository

interface AuthLocalStore {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveAuth(accessToken: String, refreshToken: String)
    suspend fun clearAuth()
}
