package com.yourcompany.digitaltok.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yourcompany.digitaltok.data.network.RetrofitClient
import com.yourcompany.digitaltok.data.network.AccountApiService
import com.yourcompany.digitaltok.data.repository.*

class AccountViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val api = RetrofitClient.create(AccountApiService::class.java)

        val localStore: AuthLocalStore = PrefsAuthLocalStore(context.applicationContext)
        val repo = AccountRepository(api, localStore)

        return AccountViewModel(repo) as T
    }
}
