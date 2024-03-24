package com.kagg886.medicine_getter.ui.screen.result

import android.graphics.Bitmap
import com.kagg886.medicine_getter.network.AIResult
import com.kagg886.medicine_getter.ui.model.BaseState
import com.kagg886.medicine_getter.ui.model.BaseUiAction

sealed class ResultScreenUiState : BaseState {
    data object DefaultState : ResultScreenUiState()
    data object LoadingState : ResultScreenUiState()
    data class LoadingSuccess(val msg: List<AIResult>) : ResultScreenUiState()
    data class LoadingFailed(val err: String) : ResultScreenUiState()
}

sealed class ResultScreenUiAction : BaseUiAction() {
    data class LoadImage(val image: Bitmap) : ResultScreenUiAction()
}