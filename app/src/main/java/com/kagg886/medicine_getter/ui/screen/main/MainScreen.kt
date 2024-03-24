package com.kagg886.medicine_getter.ui.screen.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import com.image.cropview.ImageCrop
import com.kagg886.medicine_getter.LocalHomeAction
import com.kagg886.medicine_getter.LocalNavController
import com.kagg886.medicine_getter.LocalShowSnackHost
import com.kagg886.medicine_getter.ui.screen.main.photo.PhotoComponent
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val model: MainScreenViewModel = viewModel()
    val state by model.state.collectAsState()
    val showSnackBar = LocalShowSnackHost.current

    //图片获取失败时弹出snack
    if (state is MainScreenUiState.SelectImageFailed) {
        showSnackBar((state as MainScreenUiState.SelectImageFailed).e)
    }

    //状态为selectImage时启动launcher获取数据
    val s = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        if (it == null) {
            model.dispatch(MainScreenUiAction.SelectImageFailed("用户未选择！"))
            return@rememberLauncherForActivityResult
        }
        model.dispatch(MainScreenUiAction.SelectImageSuccess(it,MainScreenUiAction.SelectImage))
    }
    if (state is MainScreenUiState.SelectingImageForPicture) {
        s.launch("image/*")
    }

    //状态为selectCamera时启动launcher
    val mediaAction by remember {
        mutableStateOf(PhotoComponent.instance)
    }
    mediaAction.Register(
        graphCallback = {
            if (it.isSuccess) {
                model.dispatch(MainScreenUiAction.SelectImageSuccess(it.uri!!,MainScreenUiAction.SelectCamera))
            } else {
                model.dispatch(MainScreenUiAction.SelectImageFailed("用户未拍照!"))
            }
        },
        permissionRationale = {
            model.dispatch(MainScreenUiAction.SelectImageFailed("用户拒绝拍照，请前往设置页面给予该应用相机权限！"))
        }
    )
    if (state is MainScreenUiState.SelectingImageForCamera) {
        mediaAction.takePhoto()
    }

    //带页面的State放在这里
    when (state) {
        is MainScreenUiState.WaitSelect -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HomeCard(onClick = { model.dispatch(MainScreenUiAction.SelectImage) }, text = "从图片中选择", icon = {
                    Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "")
                })

                HomeCard(onClick = { model.dispatch(MainScreenUiAction.SelectCamera) }, text = "从相机中选择", icon = {
                    Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "")
                })

            }
        }

        is MainScreenUiState.SelectImageSuccess<*> -> {
            val ctx = LocalContext.current
            val uri = (state as MainScreenUiState.SelectImageSuccess<*>).uri

            val action = LocalHomeAction.current
            //回传主页接口
            DisposableEffect(key1= LocalNavController.current.currentDestination!!.route) {
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
                crop.ImageCropView(modifier = Modifier
                    .weight(0.9f)
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .weight(0.1f), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
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
            val navController = LocalNavController.current
            val ctx = LocalContext.current

            val f = File(ctx.cacheDir,"a.png").apply {
                if (exists()) {
                    delete()
                }
                createNewFile()
            }
            (state as MainScreenUiState.CropImageSuccess).bitmap.run {
                compress(Bitmap.CompressFormat.PNG,80,FileOutputStream(f))
            }

            val node = navController.graph.findNode("ResultPage")
            LocalNavController.current.navigate(node!!.id)
            //最好提供uri，Bitmap由uri进行解析。
            //防止Bitmap序列化问题闪退

//            LocalNavController.current.navigate(node!!.id, bundleOf(
//                "image" to (state as MainScreenUiState.CropImageSuccess).bitmap
//            ))
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
                .padding(15.dp)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(35.dp))
            Text(text = text)
        }
    }
}