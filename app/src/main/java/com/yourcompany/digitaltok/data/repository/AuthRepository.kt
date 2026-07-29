package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.*
import com.yourcompany.digitaltok.data.network.RetrofitClient

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun signup(email: String, password: String, phone: String) =
        api.signup(SignupRequest(email = email, password = password, phoneNumber = phone))

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email = email, password = password))

    suspend fun duplicateCheck(email: String) =
        api.duplicateCheck(DuplicateCheckRequest(email))

    suspend fun resetPassword(email: String) =
        api.passwordReset(PasswordResetRequest(email))



}
