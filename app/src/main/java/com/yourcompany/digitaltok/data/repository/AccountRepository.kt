package com.yourcompany.digitaltok.data.repository

import android.util.Log
import com.yourcompany.digitaltok.data.model.EmailChangeRequest
import com.yourcompany.digitaltok.data.model.LogoutRequest
import com.yourcompany.digitaltok.data.network.AccountApiService
import com.yourcompany.digitaltok.data.network.RetrofitClient

class AccountRepository(
    private val api: AccountApiService,
    private val authLocalStore: AuthLocalStore
) {

    private val USE_FAKE_CHANGE_EMAIL_FOR_TEST = false

    suspend fun logout(): Result<Unit> = runCatching {
        val refresh = authLocalStore.getRefreshToken()
            ?: throw IllegalStateException("refreshToken is null")

        val res = api.logout(LogoutRequest(refresh))
        if (!res.isSuccess) throw RuntimeException(res.message)


        authLocalStore.clearAuth()
        RetrofitClient.clearCache()
    }

    suspend fun withdraw(): Result<Unit> = runCatching {
        Log.d("Withdraw", "REQUEST: DELETE /users/me")

        val res = api.withdraw()

        Log.d(
            "Withdraw",
            "RESPONSE: isSuccess=${res.isSuccess}, code=${res.code}, message=${res.message}"
        )

        if (!res.isSuccess) throw RuntimeException(res.message)


        authLocalStore.clearAuth()
        RetrofitClient.clearCache()
        Log.d("Withdraw", "LOCAL AUTH and CACHE CLEARED")
    }


    suspend fun changeEmail(password: String, newEmail: String): Result<Unit> = runCatching {
        val res = api.changeEmail(EmailChangeRequest(password, newEmail))
        if (!res.isSuccess) throw RuntimeException(res.message ?: "changeEmail failed")
    }

}
