package com.nkt.operatorsapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.MainUiState
import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

sealed interface AuthUiState {
    object Loading : AuthUiState
    data class Loaded(val user: User) : AuthUiState
    object NotSignedIn : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUserSignedIn = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val isUserSignedIn = _isUserSignedIn.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.isUserSignedIn()
            if (user == null) {
                _isUserSignedIn.emit(AuthUiState.NotSignedIn)
            } else {
                _isUserSignedIn.emit(AuthUiState.Loaded(user))
            }
        }
    }

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            val id = authRepository.signIn(username, password)

            if (id != null) {
                val user = authRepository.isUserSignedIn()
                if (user != null) {
                    _isUserSignedIn.emit(AuthUiState.Loaded(user))
                } else {
                    _isUserSignedIn.emit(AuthUiState.NotSignedIn)
                }
            } else {
                _isUserSignedIn.emit(AuthUiState.NotSignedIn)
            }
        }
    }

}