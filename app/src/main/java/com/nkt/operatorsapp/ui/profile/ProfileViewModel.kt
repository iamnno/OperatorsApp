package com.nkt.operatorsapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileState {
    object Loading : ProfileState
    data class Loaded(val user: User) : ProfileState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState = _profileState.asStateFlow()

    init {
        viewModelScope.launch {
            val userProfile = authRepository.getUserProfile()

            _profileState.emit(ProfileState.Loaded(userProfile))
        }

    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}