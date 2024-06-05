package com.nkt.operatorsapp.ui.operator1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.data.repositories.ParamsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirstOperatorViewModel @Inject constructor(
    private val paramsRepository: ParamsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val params = paramsRepository.getAll()
            _uiState.emit(value = UiState.Loaded(params = params))
        }
    }

    sealed interface UiState {
        object Loading : UiState
        data class Loaded(val params: Map<String, String>) : UiState
    }

}
