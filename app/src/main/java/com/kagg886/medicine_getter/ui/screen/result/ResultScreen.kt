package com.kagg886.medicine_getter.ui.screen.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kagg886.medicine_getter.DEFAULT_ROUTER
import com.kagg886.medicine_getter.LocalNavController
import java.io.File


@Composable
private fun Container(bitmap: Bitmap, effect: @Composable () -> Unit) {
    val model: ResultScreenViewModel = viewModel()
    val state by model.state.collectAsState()

    when (state) {
        ResultScreenUiState.DefaultState -> {
            LaunchedEffect(key1 = Unit, block = {
                model.dispatch(ResultScreenUiAction.LoadImage(bitmap))
            })
        }

        ResultScreenUiState.LoadingState -> {
            CircularProgressIndicator()
        }

        is ResultScreenUiState.LoadingFailed -> {
            Column {
                Text(text = (state as ResultScreenUiState.LoadingFailed).err)
                Button(onClick = { model.dispatch(ResultScreenUiAction.LoadImage(bitmap)) }) {
                    Text(text = "重试")
                }
            }
            effect()
        }

        is ResultScreenUiState.LoadingSuccess -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items((state as ResultScreenUiState.LoadingSuccess).msg) {
                    GrassListItem(item = it)
                }
            }
            effect()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen() {
    var screenSize by remember {
        mutableIntStateOf(0)
    }
    val ctx = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned {
            screenSize = it.size.height
        }) {
        val stack by LocalNavController.current.currentBackStackEntryAsState()

        var img by remember {
            mutableStateOf<Bitmap?>(null)
        }

        LaunchedEffect(key1 = stack, block = {
            img = BitmapFactory.decodeFile(File(ctx.cacheDir,"a.png").absolutePath)
        })
//
//        DisposableEffect(key1 = img, effect = {
//            onDispose {
//                img?.recycle()
//            }
//        })

        if (img != null) {
            val state = rememberBottomSheetScaffoldState()

            LaunchedEffect(key1 = Unit, block = {
                state.bottomSheetState.expand()
            })

            BottomSheetScaffold(sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(0.8f), contentAlignment = Alignment.Center
                ) {
                    Container(bitmap = img!!) {
                        val nav = LocalNavController.current
                        LaunchedEffect(key1 = state.bottomSheetState.currentValue, block = {
                            if (state.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                                nav.navigate(DEFAULT_ROUTER)
                            }
                        })
                    }
                }
            }, scaffoldState = state) {
                val density = LocalDensity.current

                val cardHeight by remember(key1 = state.bottomSheetState.requireOffset(), key2 = screenSize) {
                    mutableStateOf(with(density) {
                        screenSize.toDp() - ((screenSize - state.bottomSheetState.requireOffset()).toDp())
                    })
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight), contentAlignment = Alignment.Center
                ) {
                    Card(modifier = Modifier.height(cardHeight)) {
                        Image(bitmap = img!!.asImageBitmap(), contentDescription = "")
                    }
                }
            }
        }
    }
}