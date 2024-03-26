package com.kagg886.medicine_getter.ui

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.medicine_getter.network.AiUrl
import com.kagg886.medicine_getter.network.NetWorkClient
import com.kagg886.medicine_getter.ui.model.BaseState
import com.kagg886.medicine_getter.ui.model.BaseUiAction
import com.kagg886.medicine_getter.ui.model.BaseViewModel

private const val TAG = "ServerChooserDialog"

@Composable
fun ServerChooseDialog(onDismiss: () -> Unit) {
    val model: ServerChooserDialogModel = viewModel()
    val state by model.state.collectAsState()

    when (state) {
        ServerChooserDialogUiState.Default -> {
            LaunchedEffect(key1 = Unit) {
                Log.i(TAG, "ServerChooseDialog: 当前url配置：${AiUrl.main}")
                model.dispatch(ServerChooserDialogAction.VerifyServer(AiUrl.main, AiUrl.ocr))
            }
        }

        is ServerChooserDialogUiState.DialogShowing -> {
            var aiUrlEdit by remember {
                mutableStateOf(AiUrl.main)
            }
            var ocrUrlEdit by remember {
                mutableStateOf(AiUrl.ocr)
            }
            AlertDialog(onDismissRequest = {}, confirmButton = {
                TextButton(onClick = {
                    model.dispatch(ServerChooserDialogAction.VerifyServer(aiUrlEdit, ocrUrlEdit))
                }) {
                    Text(text = "确定")
                }
            }, title = {
                Text(text = "输入服务器地址后点击确定")
            }, text = {
                when (state) {
                    ServerChooserDialogUiState.DialogShowing.ShowEdit -> {
                        Column {
                            OutlinedTextField(value = aiUrlEdit, onValueChange = { aiUrlEdit = it }, label = {
                                Text(text = "中草药识别服务器地址")
                            })

                            OutlinedTextField(value = ocrUrlEdit, onValueChange = { ocrUrlEdit = it }, label = {
                                Text(text = "病历识别OCR地址")
                            })
                        }
                    }

                    ServerChooserDialogUiState.DialogShowing.ShowProgress -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    //永不执行
                    else -> {}
                }
            })
        }

        ServerChooserDialogUiState.Success -> {
            onDismiss()
        }
    }
}

class ServerChooserDialogModel(application: Application) :
    BaseViewModel<ServerChooserDialogUiState, ServerChooserDialogAction>(application) {
    override fun defaultUiState(): ServerChooserDialogUiState {
        return ServerChooserDialogUiState.Default
    }

    override suspend fun onAction(state: ServerChooserDialogUiState, action: ServerChooserDialogAction) {
        when (action) {
            ServerChooserDialogAction.ShowChooserDialog -> {
                setUiState(ServerChooserDialogUiState.DialogShowing.ShowEdit)
            }

            is ServerChooserDialogAction.VerifyServer -> {
                setUiState(ServerChooserDialogUiState.DialogShowing.ShowProgress)
                kotlin.runCatching {
                    if (action.aiUrlEdit.isEmpty()) {
                        setUiState(ServerChooserDialogUiState.DialogShowing.ShowEdit)
                        return
                    }
                    if (NetWorkClient(action.aiUrlEdit).execute("/echo").code == 200) {
                        AiUrl.main = action.aiUrlEdit
//                        setUiState(ServerChooserDialogUiState.Success)
                    } else {
                        setUiState(ServerChooserDialogUiState.DialogShowing.ShowEdit)
                    }
                }.onFailure {
                    Log.w(TAG, "onAction: 检查URL状态失败：url:${action.aiUrlEdit}", it)
                    setUiState(ServerChooserDialogUiState.DialogShowing.ShowEdit)
                }

                kotlin.runCatching {
                    if (action.ocrUrlEdit.isEmpty()) {
                        setUiState(ServerChooserDialogUiState.DialogShowing.ShowEdit)
                        return
                    }
                    if (NetWorkClient(action.ocrUrlEdit).execute("/ocr").body != null) {
                        AiUrl.ocr = action.ocrUrlEdit
                        setUiState(ServerChooserDialogUiState.Success)
                    } else {
                        setUiState(ServerChooserDialogUiState.DialogShowing.ShowEdit)
                    }
                }.onFailure {
                    Log.w(TAG, "onAction: 检查URL状态失败：url:${action.ocrUrlEdit}", it)
                    setUiState(ServerChooserDialogUiState.DialogShowing.ShowEdit)
                }
            }
        }
    }
}

sealed class ServerChooserDialogAction : BaseUiAction() {
    data object ShowChooserDialog : ServerChooserDialogAction()

    data class VerifyServer(
        val aiUrlEdit: String,
        val ocrUrlEdit: String,
    ) : ServerChooserDialogAction()
}

sealed interface ServerChooserDialogUiState : BaseState {
    data object Default : ServerChooserDialogUiState
    data object Success : ServerChooserDialogUiState

    sealed interface DialogShowing : ServerChooserDialogUiState {
        data object ShowEdit : DialogShowing
        data object ShowProgress : DialogShowing
    }
}