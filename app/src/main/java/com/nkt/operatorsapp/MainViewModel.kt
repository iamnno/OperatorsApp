package com.nkt.operatorsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUIState {
    object Empty : AuthUIState

    object Loading : AuthUIState
    data class SignedIn(val user: User) : AuthUIState
    object NotSignedIn : AuthUIState

    data class Failure(val message: String) : AuthUIState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUIState>(AuthUIState.Empty)
    val authState = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            val isUserSignedIn = authRepository.isUserSignedIn()
            if (isUserSignedIn) {
                val userProfile = authRepository.getUserProfile()

                _authState.emit(AuthUIState.SignedIn(user = userProfile))
            } else {
                _authState.emit(AuthUIState.NotSignedIn)
            }
        }
    }
}