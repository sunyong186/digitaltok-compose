package com.yourcompany.digitaltok.ui.setting

import androidx.lifecycle.*
import com.yourcompany.digitaltok.data.repository.AccountRepository
import kotlinx.coroutines.launch

sealed class AccountEvent {
    data object MoveToSplash : AccountEvent()
    data class ShowError(val message: String) : AccountEvent()
}

class AccountViewModel(
    private val repo: AccountRepository
) : ViewModel() {

    private val _event = MutableLiveData<AccountEvent>()
    val event: LiveData<AccountEvent> = _event

    fun logout() {
        viewModelScope.launch {
            repo.logout()
                .onSuccess {
                    _event.value = AccountEvent.MoveToSplash
                }
                .onFailure {
                    _event.value = AccountEvent.ShowError(it.message ?: "로그아웃 실패")
                }
        }
    }


    fun withdraw() {
        viewModelScope.launch {
            repo.withdraw()
                .onSuccess {
                    _event.value = AccountEvent.MoveToSplash
                }
                .onFailure {
                    _event.value = AccountEvent.ShowError(it.message ?: "회원탈퇴 실패")
                }
        }
    }
}
