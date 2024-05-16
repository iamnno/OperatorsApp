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

sealed interface MainUiState {
    object Loading : MainUiState
    data class Loaded(val user: User) : MainUiState
    object NotSignedIn : MainUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUserSignedIn = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val isUserSignedIn = _isUserSignedIn.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.isUserSignedIn()
            if (user == null) {
                _isUserSignedIn.emit(MainUiState.NotSignedIn)
            } else {
                _isUserSignedIn.emit(MainUiState.Loaded(user))
            }
        }
    }
}