package com.kagg886.medicine_getter.ui.screen.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kagg886.medicine_getter.ui.screen.details.GrassListItem

@Composable
fun HistoryScreen() {
    val model:HistoryViewModel = viewModel()

    val state by model.state.collectAsState()

    when(state) {
        HistoryUiState.Default -> {
            model.dispatch(HistoryUiAction.LoadData)
        }

        is HistoryUiState.LoadSuccess -> {
            val data = (state as HistoryUiState.LoadSuccess).list.asReversed()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(data) {
                    GrassListItem(item = it.result)
                }
            }
        }
        HistoryUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}