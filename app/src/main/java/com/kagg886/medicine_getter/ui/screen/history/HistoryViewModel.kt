package com.kagg886.medicine_getter.ui.screen.history

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.kagg886.medicine_getter.backend.AppDatabase
import com.kagg886.medicine_getter.ui.model.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(application: Application) : BaseViewModel<HistoryUiState, HistoryUiAction>(application) {
    override fun defaultUiState(): HistoryUiState {
        return HistoryUiState.Default
    }

    private val dao = AppDatabase.getDatabase(application).identificationRecordDao()

    override suspend fun onAction(state: HistoryUiState, action: HistoryUiAction) {
        when (action) {
            is HistoryUiAction.LoadData -> {
                setUiState(HistoryUiState.Loading)
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        setUiState(HistoryUiState.LoadSuccess(dao.getAllRecord().first()))
                    }
                }
            }
        }
    }
}