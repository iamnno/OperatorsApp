package com.nkt.operatorsapp.ui.operator2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.data.repositories.ParamsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState {
    object Loading : UiState
    data class Loaded(val params: Map<String, String>) : UiState
}

@HiltViewModel
class SecondOperatorViewModel @Inject constructor(
    private val paramsRepository: ParamsRepository
) : ViewModel() {

    private val _params = MutableStateFlow<UiState>(UiState.Loading)
    val params = _params.asStateFlow()

    init {
        viewModelScope.launch {
            val params = paramsRepository.getAll()
            _params.emit(UiState.Loaded(params = params))
        }
    }

    fun update(params: Map<String, String>) {
        viewModelScope.launch {
            paramsRepository.updateAll(params)
        }
    }
}