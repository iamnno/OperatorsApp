package com.nkt.operatorsapp.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.data.repositories.QuestionnaireRepository
import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.data.UserType
import com.nkt.operatorsapp.data.repositories.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

sealed interface UsersUiState {
    object Loading : UsersUiState
    data class Loaded(
        val users: List<User>,
        val queries: List<String>
    ) : UsersUiState
}

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val questionnaireRepository: QuestionnaireRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val users = usersRepository.getAll()
            val queries = questionnaireRepository.getAll()

            _uiState.emit(value = UsersUiState.Loaded(users, queries))
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            usersRepository.delete(user)

            val users = usersRepository.getAll()
            _uiState.emit(value = (_uiState.value as UsersUiState.Loaded).copy(users = users))
        }
    }

    fun createUser(username: String, type: UserType, password: String) {
        viewModelScope.launch {
            usersRepository.create(
                username = username, type = type, hash = generateHash(password)
            )
        }
    }

    private fun generateHash(password: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(password, salt)
    }

    fun deleteQuery(query: String) {
        viewModelScope.launch {
            questionnaireRepository.delete(query)

            val queries = questionnaireRepository.getAll()
            _uiState.emit(value = (_uiState.value as UsersUiState.Loaded).copy(queries = queries))
        }
    }

}