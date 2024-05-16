package com.nkt.operatorsapp.ui.questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkt.operatorsapp.data.repositories.QuestionnaireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val questionnaireRepository: QuestionnaireRepository
) : ViewModel() {

    private val _sentState = MutableSharedFlow<Boolean>()
    val sentState = _sentState.asSharedFlow()

    fun send(keyWord: String) {
        viewModelScope.launch {
            _sentState.emit(value = questionnaireRepository.save(keyWord))
        }
    }
}