package com.kagg886.medicine_getter.ui.screen.history

import com.kagg886.medicine_getter.backend.entity.IdentificationRecord
import com.kagg886.medicine_getter.ui.model.BaseState
import com.kagg886.medicine_getter.ui.model.BaseUiAction

sealed class HistoryUiState : BaseState {
    data object Default : HistoryUiState()

    data object Loading : HistoryUiState()

    data class LoadSuccess(val list: List<IdentificationRecord>):HistoryUiState()
}

sealed class HistoryUiAction : BaseUiAction() {
    data object LoadData : HistoryUiAction()
}