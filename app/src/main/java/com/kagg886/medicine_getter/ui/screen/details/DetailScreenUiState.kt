package com.kagg886.medicine_getter.ui.screen.details

import android.graphics.Bitmap
import com.kagg886.medicine_getter.network.AIResult
import com.kagg886.medicine_getter.ui.model.BaseState
import com.kagg886.medicine_getter.ui.model.BaseUiAction

sealed class DetailScreenUiState : BaseState {
    data object DefaultState : DetailScreenUiState()
    data object LoadingState : DetailScreenUiState()
    data class LoadingSuccess(val msg: List<AIResult>) : DetailScreenUiState()
    data class LoadingFailed(val err: String) : DetailScreenUiState()
}

sealed class DetailScreenUiAction : BaseUiAction() {
    data class LoadImage(val image: Bitmap) : DetailScreenUiAction()
}