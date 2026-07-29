package com.yourcompany.digitaltok.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.model.UserMeResult
import com.yourcompany.digitaltok.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileEditUiState(
    val isLoading: Boolean = false,
    val nickname: String = "",
    val email: String = "",
    val errorMessage: String? = null
)

class ProfileEditViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState

    fun loadMyProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            userRepository.getMyProfile()
                .onSuccess { me: UserMeResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        nickname = me.nickname,
                        email = me.email
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "프로필 정보를 불러오지 못했습니다."
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
