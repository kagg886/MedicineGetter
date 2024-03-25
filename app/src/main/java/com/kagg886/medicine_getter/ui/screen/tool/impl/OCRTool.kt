package com.kagg886.medicine_getter.ui.screen.tool.impl

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.image.cropview.ImageCrop
import com.kagg886.medicine_getter.LocalHomeAction
import com.kagg886.medicine_getter.LocalNavController
import com.kagg886.medicine_getter.LocalShowSnackHost
import com.kagg886.medicine_getter.R
import com.kagg886.medicine_getter.ui.model.BaseState
import com.kagg886.medicine_getter.ui.model.BaseUiAction
import com.kagg886.medicine_getter.ui.model.BaseViewModel
import com.kagg886.medicine_getter.ui.screen.main.MainScreenUiAction
import com.kagg886.medicine_getter.ui.screen.main.MainScreenUiState
import com.kagg886.medicine_getter.ui.screen.main.MainScreenViewModel
import com.kagg886.medicine_getter.ui.screen.main.photo.PhotoComponent

@Composable
fun OCRToolResult(bitmap: Bitmap) {
    val model: OCRToolResultViewModel = viewModel()
    val state by model.state.collectAsState()

    when (state) {
        OCRToolResultUiState.Default -> {
            LaunchedEffect(key1 = Unit, block = {
                model.dispatch(OCRToolResultAction.Loading(bitmap))
            })
        }

        OCRToolResultUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is OCRToolResultUiState.LoadingFailed -> {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "出错了!:${(state as OCRToolResultUiState.LoadingFailed).msg}")
                Button(onClick = { model.dispatch(OCRToolResultAction.Loading(bitmap)) }) {
                    Text(text = "重试")
                }
            }
        }

        is OCRToolResultUiState.LoadingSuccess -> {
            TODO("ocr识别详情")
        }
    }
}

class OCRToolResultViewModel(application: Application) : BaseViewModel<OCRToolResultUiState, OCRToolResultAction>(
    application
) {
    override fun defaultUiState(): OCRToolResultUiState = OCRToolResultUiState.Default


    override suspend fun onAction(state: OCRToolResultUiState, action: OCRToolResultAction) {
        when (action) {
            is OCRToolResultAction.Loading -> {
                setUiState(OCRToolResultUiState.Loading)
                //TODO do network
            }
        }
    }

}

sealed interface OCRToolResultUiState : BaseState {
    data object Default : OCRToolResultUiState

    data object Loading : OCRToolResultUiState

    data class LoadingSuccess(val s: String) : OCRToolResultUiState
    data class LoadingFailed(val msg: String) : OCRToolResultUiState
}

sealed class OCRToolResultAction : BaseUiAction() {
    data class Loading(val bitmap: Bitmap) : OCRToolResultAction()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRTool() {
    val model: MainScreenViewModel = viewModel()
    val state by model.state.collectAsState()
    val showSnackBar = LocalShowSnackHost.current

    //图片获取失败时弹出snack
    if (state is MainScreenUiState.SelectImageFailed) {
        showSnackBar((state as MainScreenUiState.SelectImageFailed).e)
    }

    val s = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        if (it == null) {
            model.dispatch(MainScreenUiAction.SelectImageFailed("用户未选择！"))
            return@rememberLauncherForActivityResult
        }
        model.dispatch(MainScreenUiAction.SelectImageSuccess(it, MainScreenUiAction.SelectImage))
    }
    if (state is MainScreenUiState.SelectingImageForPicture) {
        //状态为selectImage时启动launcher获取数据
        s.launch("image/*")
    }

    //状态为selectCamera时启动launcher

    if (state is MainScreenUiState.SelectingImageForCamera) {
        val mediaAction by remember {
            mutableStateOf(PhotoComponent.instance)
        }
        mediaAction.Register(
            graphCallback = {
                if (it.isSuccess) {
                    model.dispatch(MainScreenUiAction.SelectImageSuccess(it.uri!!, MainScreenUiAction.SelectCamera))
                } else {
                    model.dispatch(MainScreenUiAction.SelectImageFailed("用户未拍照!"))
                }
            },
            permissionRationale = {
                model.dispatch(MainScreenUiAction.SelectImageFailed("用户拒绝拍照，请前往设置页面给予该应用相机权限！"))
            }
        )
        mediaAction.takePhoto()
    }

    //带页面的State放在这里
    when (state) {
        is MainScreenUiState.WaitSelect -> {
            val nav = LocalNavController.current
            ModalBottomSheet(onDismissRequest = {
                nav.popBackStack()
            }) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    HomeCard(onClick = { model.dispatch(MainScreenUiAction.SelectImage) }, text = "从图片中选择", icon = {
                        Icon(painter = painterResource(id = R.drawable.baseline_insert_photo_24), contentDescription = "")
                    })

                    HomeCard(onClick = { model.dispatch(MainScreenUiAction.SelectCamera) }, text = "从相机中选择", icon = {
                        Icon(painter = painterResource(id = R.drawable.baseline_camera_alt_24), contentDescription = "")
                    })
                }
            }
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                HomeCard(onClick = { model.dispatch(MainScreenUiAction.SelectImage) }, text = "从图片中选择", icon = {
//                    Icon(painter = painterResource(id = R.drawable.baseline_insert_photo_24), contentDescription = "")
//                })
//
//                HomeCard(onClick = { model.dispatch(MainScreenUiAction.SelectCamera) }, text = "从相机中选择", icon = {
//                    Icon(painter = painterResource(id = R.drawable.baseline_camera_alt_24), contentDescription = "")
//                })
//
//            }
        }

        is MainScreenUiState.SelectImageSuccess<*> -> {
            val ctx = LocalContext.current
            val uri = (state as MainScreenUiState.SelectImageSuccess<*>).uri

            val action = LocalHomeAction.current
            //回传主页接口
            DisposableEffect(key1 = LocalNavController.current.currentDestination!!.route) {
                action.value = {
                    model.dispatch(MainScreenUiAction.Reset)
                }
                //跳出页面时清除主页图标
                onDispose {
                    action.value = null
                }
            }
            val bitmap by remember(uri) {
                mutableStateOf(BitmapFactory.decodeStream(ctx.contentResolver.openInputStream(uri)))
            }
            val crop = ImageCrop(bitmap)
            Column(modifier = Modifier.fillMaxSize()) {
                crop.ImageCropView(
                    modifier = Modifier
                        .weight(0.9f)
                        .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .weight(0.1f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { crop.resetView() }) {
                        Text(text = "还原")
                    }
                    Button(onClick = {
                        val cropBitmap = crop.onCrop()
                        model.dispatch(MainScreenUiAction.CropImageSuccess(cropBitmap))
                    }) {
                        Text(text = "确认")
                    }
                    Button(onClick = {
                        model.dispatch((state as MainScreenUiState.SelectImageSuccess<*>).source)
                    }) {
                        Text(text = "重选")
                    }
                }
            }
        }

        is MainScreenUiState.CropImageSuccess -> {
            OCRToolResult((state as MainScreenUiState.CropImageSuccess).bitmap)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCard(onClick: () -> Unit, icon: @Composable () -> Unit = {}, text: String) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.padding(25.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(35.dp))
            Text(text = text)
        }
    }
}