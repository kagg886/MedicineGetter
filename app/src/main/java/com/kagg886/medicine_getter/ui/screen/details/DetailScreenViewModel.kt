package com.kagg886.medicine_getter.ui.screen.details

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.kagg886.medicine_getter.BuildConfig
import com.kagg886.medicine_getter.backend.AppDatabase
import com.kagg886.medicine_getter.backend.entity.IdentificationRecord
import com.kagg886.medicine_getter.network.AIResult
import com.kagg886.medicine_getter.network.getAIResult
import com.kagg886.medicine_getter.ui.model.BaseViewModel
import com.kagg886.sylu_eoa.api.v2.network.NetWorkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class DetailScreenViewModel(application: Application) :
    BaseViewModel<DetailScreenUiState, DetailScreenUiAction>(application = application) {

    private val net: NetWorkClient = NetWorkClient(BuildConfig.AI_HOST)

    private val dao = AppDatabase.getDatabase(getApplication()).identificationRecordDao()

    override fun defaultUiState(): DetailScreenUiState {
        return DetailScreenUiState.DefaultState
    }


    override suspend fun onAction(state: DetailScreenUiState, action: DetailScreenUiAction) {
        when (action) {
            is DetailScreenUiAction.LoadImage -> {
                setUiState(DetailScreenUiState.LoadingState)
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
                            setUiState(DetailScreenUiState.LoadingSuccess(result))
                            result.forEach {
                                dao.insertNewRecord(IdentificationRecord(result = it))
                            }
                        }.onFailure {
                            setUiState(DetailScreenUiState.LoadingFailed(it.message ?: "未知错误"))
                        }
                    }
                }
            }
        }
    }
}