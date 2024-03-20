package com.kagg886.medicine_getter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kagg886.medicine_getter.network.AiUrl
import com.kagg886.medicine_getter.ui.ServerChooseDialog
import com.kagg886.medicine_getter.ui.screen.history.HistoryScreen
import com.kagg886.medicine_getter.ui.screen.details.DetailScreen
import com.kagg886.medicine_getter.ui.screen.main.MainScreen
import com.kagg886.medicine_getter.ui.screen.ocr.OcrScreen
import com.kagg886.medicine_getter.ui.theme.MedicineGetterTheme
import kotlinx.coroutines.launch

val LocalNavController = compositionLocalOf<NavHostController> {
    error("NavController not provided")
}

val LocalShowSnackHost = compositionLocalOf<(s: String) -> Unit> {
    error("NavController not provided")
}

val LocalHomeAction = compositionLocalOf<MutableState<(() -> Unit)?>> {
    mutableStateOf(null)
}


const val DEFAULT_ROUTER = "MainPage"

object PageConfig {
    val nav = listOf(
        PageItem("首页", R.drawable.baseline_home_24, DEFAULT_ROUTER) @Composable { MainScreen() },
        PageItem("识别", R.drawable.baseline_home_24, "OCRPage") @Composable { OcrScreen() },
        PageItem("历史", R.drawable.baseline_home_24, "HistoryPage") @Composable { HistoryScreen() },
    )

    val allPage: List<PageItem> = mutableListOf<PageItem>().apply {
        addAll(nav)
        add(PageItem("详情", R.drawable.baseline_home_24, "DetailPage") @Composable { DetailScreen() })
    }
}

data class PageItem(val title: String, val icon: Int = 0, val router: String, val widget: @Composable () -> Unit)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicineGetterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val nav = rememberNavController()
                    val reg by nav.currentBackStackEntryAsState()

                    val snackHostState by remember {
                        mutableStateOf(SnackbarHostState())
                    }

                    var serverChooser by remember {
                        mutableStateOf(true)
                    }

                    if (serverChooser) {
                        ServerChooseDialog {
                            serverChooser = false
                        }
                    }

                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(snackHostState) { data ->
                                Snackbar(
                                    snackbarData = data,
                                    shape = CutCornerShape(10.dp)
                                )
                            }
                        },
                        topBar = {
                            TopAppBar(
                                title = { Text(text = LocalContext.current.resources.getString(R.string.app_name)) },
                                navigationIcon = {
                                    val s = LocalHomeAction.current
                                    val func = s.value
                                    if (func != null) {
                                        IconButton(
                                            onClick = {
                                                func()
                                                s.value = null
                                            }
                                        ) {
                                            Icon(Icons.Outlined.Home, null)
                                        }
                                    }
                                })
                        },
                        bottomBar = {
                            NavigationBar {
                                PageConfig.nav.forEach { entry ->
                                    val select = entry.router == (reg?.destination?.route ?: DEFAULT_ROUTER)
                                    NavigationBarItem(
                                        icon = {
                                            Icon(painter = painterResource(entry.icon), "")
                                        },
                                        label = {
                                            Text(entry.title)
                                        },
                                        selected = select,
                                        onClick = {
                                            if (!select) {
                                                nav.navigate(entry.router)
                                            }
                                        },
                                        alwaysShowLabel = false
                                    )
                                }
                            }
                        }) {
                        val scope = rememberCoroutineScope()
                        CompositionLocalProvider(
                            LocalNavController provides nav,
                            LocalShowSnackHost provides {
                                scope.launch {
                                    snackHostState.showSnackbar(it)
                                }
                            },
                        ) {
                            NavHost(
                                navController = nav,
                                startDestination = DEFAULT_ROUTER,
                                modifier = Modifier
                                    .padding(it)
                                    .fillMaxSize(),
                            ) {
                                PageConfig.allPage.forEach { entry ->
                                    composable(entry.router) {
                                        entry.widget()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}