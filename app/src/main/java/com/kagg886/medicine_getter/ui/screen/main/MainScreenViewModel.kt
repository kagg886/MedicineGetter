package com.kagg886.medicine_getter.ui.screen.main

import android.app.Application
import com.kagg886.medicine_getter.ui.model.BaseViewModel

class MainScreenViewModel(application: Application) :
    BaseViewModel<MainScreenUiState, MainScreenUiAction>(application) {
    override fun defaultUiState(): MainScreenUiState {
        return MainScreenUiState.WaitSelect()
    }

    override suspend fun onAction(state: MainScreenUiState, action: MainScreenUiAction) {
        val newState = when (action) {
            MainScreenUiAction.SelectImage -> {
                MainScreenUiState.SelectingImageForPicture
            }

            is MainScreenUiAction.SelectImageSuccess -> {
                MainScreenUiState.SelectImageSuccess(action.uri, action.source)
            }

            is MainScreenUiAction.SelectImageFailed -> {
                MainScreenUiState.SelectImageFailed(action.err)
            }

            MainScreenUiAction.Reset -> {
                defaultUiState()
            }

            MainScreenUiAction.SelectCamera -> {
                MainScreenUiState.SelectingImageForCamera
            }

            is MainScreenUiAction.CropImageSuccess -> {
                MainScreenUiState.CropImageSuccess(action.bitmap)
            }
        }
        setUiState(newState)
    }
}