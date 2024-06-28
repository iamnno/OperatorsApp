package com.nkt.operatorsapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.AuthUIState
import com.nkt.operatorsapp.data.UserNotExistsException
import com.nkt.operatorsapp.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUIState>(AuthUIState.Empty)
    val authState = _authState.asStateFlow()

    fun signIn(username: String, password: String) {
        viewModelScope.launch {
            try {
                val userProfile = authRepository.signIn(username, password)
                
                _authState.emit(AuthUIState.SignedIn(userProfile))
            } catch (e: UserNotExistsException) {
                _authState.emit(
                    AuthUIState.Failure(
                        e.message ?: "User with given credentials doesn't not exists"
                    )
                )
            } catch (e: IllegalStateException) {
                _authState.emit(AuthUIState.Failure("Error occurred while signing in!"))
            }
        }
    }

}