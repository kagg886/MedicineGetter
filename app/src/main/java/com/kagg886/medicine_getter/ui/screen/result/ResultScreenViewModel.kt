package com.kagg886.medicine_getter.ui.screen.result

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.kagg886.medicine_getter.backend.AppDatabase
import com.kagg886.medicine_getter.backend.entity.IdentificationRecord
import com.kagg886.medicine_getter.network.AIResult
import com.kagg886.medicine_getter.network.AiUrl
import com.kagg886.medicine_getter.network.getAIResult
import com.kagg886.medicine_getter.ui.model.BaseViewModel
import com.kagg886.medicine_getter.network.NetWorkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ResultScreenViewModel(application: Application) :
    BaseViewModel<ResultScreenUiState, ResultScreenUiAction>(application = application) {

    private val net: NetWorkClient = NetWorkClient(AiUrl.main)

    private val dao = AppDatabase.getDatabase(getApplication()).identificationRecordDao()

    override fun defaultUiState(): ResultScreenUiState {
        return ResultScreenUiState.DefaultState
    }


    override suspend fun onAction(state: ResultScreenUiState, action: ResultScreenUiAction) {
        when (action) {
            is ResultScreenUiAction.LoadImage -> {
                setUiState(ResultScreenUiState.LoadingState)
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        kotlin.runCatching {
                            val result = net.getAIResult(action.image.let {
                                val s = ByteArrayOutputStream()
                                it.compress(Bitmap.CompressFormat.PNG, 80, s)
                                ByteArrayInputStream(s.toByteArray())
                            }).jsonArray.map {
                                Json.decodeFromJsonElement<AIResult>(it.jsonObject)
                            }
                            assert(result.isNotEmpty()) {
                                "列表为空！"
                            }
                            //保存到数据库
                            setUiState(ResultScreenUiState.LoadingSuccess(result))
                            result.forEach {
                                dao.insertNewRecord(IdentificationRecord(result = it))
                            }
                        }.onFailure {
                            setUiState(ResultScreenUiState.LoadingFailed(it.message ?: "未知错误"))
                        }
                    }
                }
            }
        }
    }
}