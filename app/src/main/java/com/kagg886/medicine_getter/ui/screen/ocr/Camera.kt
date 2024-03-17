package com.kagg886.medicine_getter.ui.screen.ocr

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraViewPermission(
    modifier: Modifier = Modifier,
    preview: Preview,
    imageCapture: ImageCapture? = null,
    imageAnalysis: ImageAnalysis? = null,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    enableTorch: Boolean = false,
    focusOnTap: Boolean = false,
) {

    val context = LocalContext.current

    PermissionView(
        permission = Manifest.permission.CAMERA,
        rationale = "请打开相机权限",
        permissionNotAvailableContent = {
            Column(modifier) {
                Text("未能获取相机")
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        openSettingsPermission(context)
                    }
                ) {
                    Text("打开应用权限设置")
                }
            }
        }
    ) {

        CameraView(
            modifier,
            preview = preview,
            imageCapture = imageCapture,
            imageAnalysis = imageAnalysis,
            scaleType = scaleType,
            cameraSelector = cameraSelector,
            focusOnTap = focusOnTap,
            enableTorch = enableTorch,
        )


    }


}


@ExperimentalPermissionsApi
@Composable
fun PermissionView(
    permission: String = android.Manifest.permission.CAMERA,
    rationale: String = "该功能需要此权限，请打开该权限。",
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { },
) {
    val permissionState = rememberPermissionState(permission)
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            Rationale(
                text = rationale,
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        },
        permissionNotAvailableContent = permissionNotAvailableContent,
        content = content
    )
}


fun ImageCapture.takePhoto(
    executor: Executor = Executors.newSingleThreadExecutor(),
    onImageCaptured: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit,
) {

    val stream = ByteArrayOutputStream()
    val outputOptions = ImageCapture.OutputFileOptions.Builder(stream).build()

    takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val byt = stream.toByteArray()
            onImageCaptured(BitmapFactory.decodeByteArray(byt, 0, byt.size))
        }
    })
}


@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = "请求权限")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("确定")
            }
        }
    )
}

fun openSettingsPermission(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    )
}

// https://stackoverflow.com/a/70302763
@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    preview: Preview,
    imageCapture: ImageCapture? = null,
    imageAnalysis: ImageAnalysis? = null,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    enableTorch: Boolean = false,
    focusOnTap: Boolean = false,
) {

    val context = LocalContext.current

    //1
    val previewView = remember { PreviewView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProvider by produceState<ProcessCameraProvider?>(initialValue = null) {
        value = context.getCameraProvider()
    }

    val camera = remember(cameraProvider) {
        cameraProvider?.let {
            it.unbindAll()
            it.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                *listOfNotNull(preview, imageAnalysis, imageCapture).toTypedArray()
            )
        }
    }


    // 2
    LaunchedEffect(true) {
        preview.setSurfaceProvider(previewView.surfaceProvider)
        previewView.scaleType = scaleType
    }


    LaunchedEffect(camera, enableTorch) {
        // 控制闪光灯
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                it.cameraControl.enableTorch(context, enableTorch)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    // 3
    AndroidView(
        { previewView },
        modifier = modifier
            .fillMaxSize()
            .pointerInput(camera, focusOnTap) {
                if (!focusOnTap) return@pointerInput

                detectTapGestures {
                    val meteringPointFactory = SurfaceOrientedMeteringPointFactory(
                        size.width.toFloat(),
                        size.height.toFloat()
                    )

                    // 点击屏幕聚焦
                    val meteringAction = FocusMeteringAction
                        .Builder(
                            meteringPointFactory.createPoint(it.x, it.y),
                            FocusMeteringAction.FLAG_AF
                        )
                        .disableAutoCancel()
                        .build()

                    camera?.cameraControl?.startFocusAndMetering(meteringAction)
                }
            },
    )
}


private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private suspend fun CameraControl.enableTorch(context: Context, torch: Boolean): Unit =
    suspendCoroutine {
        enableTorch(torch).addListener(
            {},
            ContextCompat.getMainExecutor(context)
        )
    }
