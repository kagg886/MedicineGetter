package com.kagg886.medicine_getter.ui.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.SubcomposeAsyncImage
import com.kagg886.medicine_getter.DEFAULT_ROUTER
import com.kagg886.medicine_getter.LocalHomeAction
import com.kagg886.medicine_getter.LocalNavController
import com.kagg886.medicine_getter.network.AIResult
import com.kagg886.medicine_getter.network.AiUrl
import com.kagg886.medicine_getter.ui.theme.Typography
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

private val json = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun DetailScreen() {
    val stack by LocalNavController.current.currentBackStackEntryAsState()

    val item by remember(stack) {
        mutableStateOf(stack?.arguments?.getSerializable("id") as AIResult?)
    }

    val ctx = LocalContext.current
    val items = remember {
        json.decodeFromStream<Map<String, GrassItem>>(ctx.assets.open("details.json"))
    }

    val action = LocalHomeAction.current
    val nav = LocalNavController.current

    DisposableEffect(key1 = stack, effect = {
        action.value = {
            nav.navigate(DEFAULT_ROUTER)
        }
        onDispose {
            action.value = null
        }
    })
    if (item != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            val unit = items[item!!.spell]!!

            SubcomposeAsyncImage(
                model = "${AiUrl.host}/ai/image?spell=${item!!.spell}",
                loading = {
                    CircularProgressIndicator() // 圆形进度条
                },
                error = {
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = "")
                },
                modifier = Modifier.width(200.dp).align(Alignment.CenterHorizontally),
                contentDescription = ""
            )

            Text(text = item!!.name, style = Typography.titleLarge, fontSize = 30.sp)
            Divider()
            Spacer(modifier = Modifier.height(15.dp))
            val handler = LocalUriHandler.current
            val annotatedString = buildAnnotatedString {
                append(unit.information)

                pushStringAnnotation(
                    tag = "URL",  // 注解的标签
                    annotation = unit.link  // 链接地址
                )
                pushStyle(
                    style = SpanStyle(
                        color = Color.Blue,  // 文本颜色
                        fontSize = 20.sp,  // 字体大小
                        textDecoration = TextDecoration.Underline  // 下划线装饰
                    )
                )
                withStyle(SpanStyle(fontSize = 20.sp)) {
                    append("...查看更多")  // 超链接的文本
                }
            }

            ClickableText(text = annotatedString, onClick = { offset ->
                annotatedString.getStringAnnotations("URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        handler.openUri(annotation.item)
                    }
            })
        }
    }
}

@Serializable
data class GrassItem(
    val information: String,
    val link: String,
)