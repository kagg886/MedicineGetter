package com.kagg886.medicine_getter.ui.screen.main

import android.graphics.Bitmap
import android.net.Uri
import com.kagg886.medicine_getter.ui.model.BaseState
import com.kagg886.medicine_getter.ui.model.BaseUiAction

open class MainScreenUiState : BaseState {

    //等待点击按钮
    open class WaitSelect : MainScreenUiState()

    //获取图片中/获取图片失败
    object SelectingImageForPicture : WaitSelect()
    object SelectingImageForCamera : WaitSelect()
    data class SelectImageSuccess<T : MainScreenUiAction.ImageSelectAction>(val uri: Uri, val source: T) :
        MainScreenUiState()

    data class SelectImageFailed(val e: String) : WaitSelect()

    data class CropImageSuccess(val bitmap: Bitmap) : MainScreenUiState()
}

sealed class MainScreenUiAction : BaseUiAction() {
    sealed class ImageSelectAction : MainScreenUiAction()

    data object Reset : MainScreenUiAction()

    data object SelectImage : ImageSelectAction()
    data object SelectCamera : ImageSelectAction()
    data class SelectImageSuccess(val uri: Uri, val source: ImageSelectAction) : MainScreenUiAction()
    data class SelectImageFailed(val err: String) : MainScreenUiAction()


    data class CropImageSuccess(val bitmap: Bitmap) : MainScreenUiAction()
}