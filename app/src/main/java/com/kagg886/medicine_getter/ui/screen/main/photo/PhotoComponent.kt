package com.kagg886.medicine_getter.ui.screen.main.photo

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoComponent {

    private var takePhotoLauncher: ManagedActivityResultLauncher<Unit?, PictureResult>? = null

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        val instance get() = Helper.obj
    }

    private object Helper {
        val obj = PhotoComponent()
    }

    //监听拍照权限flow
    private val checkCameraPermission =
        MutableSharedFlow<Boolean?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private fun setCheckCameraPermissionState(value: Boolean?) {
        scope.launch {
            checkCameraPermission.emit(value)
        }
    }

    /**
     * @param galleryCallback 相册结果回调
     * @param graphCallback 拍照结果回调
     * @param permissionRationale 权限拒绝状态回调
     **/
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun Register(
        graphCallback: (graphResult: PictureResult) -> Unit,
        permissionRationale: ((gallery: Boolean) -> Unit)? = null,
    ) {
        val rememberGraphCallback = rememberUpdatedState(newValue = graphCallback)

        takePhotoLauncher = rememberLauncherForActivityResult(contract = TakePhoto.instance) {
            rememberGraphCallback.value.invoke(it)
        }

        var permissionCameraState by rememberSaveable { mutableStateOf(false) }
        val permissionList = arrayListOf(
            Manifest.permission.CAMERA,
        )
        val cameraPermissionState = rememberMultiplePermissionsState(permissionList)
        LaunchedEffect(Unit) {
            checkCameraPermission.collectLatest {
                permissionCameraState = it == true
                if (it == true) {
                    if (cameraPermissionState.allPermissionsGranted) {
                        setCheckCameraPermissionState(null)
                        takePhotoLauncher?.launch(null)
                    } else if (cameraPermissionState.shouldShowRationale) {
                        setCheckCameraPermissionState(null)
                        permissionRationale?.invoke(false)
                    } else {
                        cameraPermissionState.launchMultiplePermissionRequest()
                    }
                }
            }
        }

        LaunchedEffect(cameraPermissionState.allPermissionsGranted) {
            if (cameraPermissionState.allPermissionsGranted && permissionCameraState) {
                setCheckCameraPermissionState(null)
                takePhotoLauncher?.launch(null)
            }
        }

    }

    //调用拍照
    fun takePhoto() {
        setCheckCameraPermissionState(true)
    }

}
