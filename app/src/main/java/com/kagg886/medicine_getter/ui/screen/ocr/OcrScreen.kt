package com.kagg886.medicine_getter.ui.screen.ocr

import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kagg886.medicine_getter.backend.AppDatabase
import com.kagg886.medicine_getter.backend.entity.IdentificationRecord
import com.kagg886.medicine_getter.network.AIResult
import com.kagg886.medicine_getter.network.AiUrl
import com.kagg886.medicine_getter.network.getAIResult
import com.kagg886.medicine_getter.network.NetWorkClient
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Composable
fun OcrScreen() {
    val api by remember {
        mutableStateOf(ImageCapture.Builder().build())
    }
    val snack by remember {
        mutableStateOf(SnackbarHostState())
    }
    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snack) {
            Snackbar(snackbarData = it)
        }
    }) {
        CameraViewPermission(modifier = Modifier.padding(it),preview = Preview.Builder().build(), imageCapture = api)
    }

    val ctx = LocalContext.current
    DisposableEffect(key1 = Unit) {
        val dispatcher = CoroutineScope(Dispatchers.IO)

        dispatcher.launch dispatcher@{
            while (isActive) {
                delay(3000) //防止内存溢出
                api.takePhoto(onImageCaptured = {bitmap->
                    dispatcher.launch {
                        if (!isActive) {
                            //上传前再检测一下
                            return@launch
                        }
                        val result = NetWorkClient(AiUrl.host).getAIResult(bitmap.let {
                            val s = ByteArrayOutputStream()
                            it.compress(Bitmap.CompressFormat.PNG, 80, s)
                            it.recycle()
                            ByteArrayInputStream(s.toByteArray())
                        }).jsonArray.map {
                            Json.decodeFromJsonElement<AIResult>(it.jsonObject)
                        }
                        result.forEach {
                            AppDatabase.getDatabase(ctx).identificationRecordDao().insertNewRecord(
                                IdentificationRecord(result = it)
                            )
                        }
                        if (result.isNotEmpty()) {
                            snack.showSnackbar("找到了${result.size}条识别结果，请前往历史记录查看")
                        }
                    }
                }, onError = {

                })
            }
        }

        onDispose {
            dispatcher.cancel()
        }
    }
}