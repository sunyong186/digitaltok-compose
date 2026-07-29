package com.yourcompany.digitaltok.data.model


data class SignupRequest(
    val email: String,
    val password: String,
    val phoneNumber: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class DuplicateCheckRequest(
    val email: String
)

data class PasswordResetRequest(
    val email: String
)

data class LogoutRequest(
    val refreshToken: String
)

data class SignupResult(
    val userId: Int,
    val email: String,
    val nickname: String,
    val accessToken: String,
    val refreshToken: String
)

data class LoginResult(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)
