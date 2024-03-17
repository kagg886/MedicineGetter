package com.kagg886.medicine_getter.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<UiState, UiAction>(application: Application) : AndroidViewModel(application)
        where UiState : BaseState,
              UiAction : BaseUiAction {
    private val _state: MutableStateFlow<UiState> = MutableStateFlow(defaultUiState())

    val state = _state.asStateFlow()


    fun dispatch(action: UiAction) {
        viewModelScope.launch {
            onAction(_state.value, action)
        }
    }

    protected fun setUiState(state:UiState) {
        _state.value = state
    }

    internal abstract fun defaultUiState(): UiState
    internal abstract suspend fun onAction(state: UiState, action: UiAction)
}

open class BaseUiAction
interface BaseState