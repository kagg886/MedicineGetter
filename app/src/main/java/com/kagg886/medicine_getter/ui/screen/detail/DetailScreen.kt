package com.kagg886.medicine_getter.ui.screen.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.kagg886.medicine_getter.DEFAULT_ROUTER
import com.kagg886.medicine_getter.LocalHomeAction
import com.kagg886.medicine_getter.LocalNavController
import com.kagg886.medicine_getter.network.AIResult
import com.kagg886.medicine_getter.network.AiUrl
import com.kagg886.medicine_getter.network.readAllOnLowerAndroid
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
        json.decodeFromStream<Map<String,GrassItem>>(ctx.assets.open("details.json"))
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                val handler = LocalUriHandler.current
                val annotatedString = buildAnnotatedString {
                    // 为部分文本添加样式并附上注解(超链接)
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
                    append(item!!.name)  // 超链接的文本
                    pop()  // 结束样式
                }
                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations("URL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                handler.openUri(annotation.item)
                            }
                    }
                )
//                Text(text = item!!.name, fontStyle= Typography.titleLarge.fontStyle)

                SubcomposeAsyncImage(
                    model = "${AiUrl.host}/ai/image?spell=${item!!.spell}",
                    loading = {
                        CircularProgressIndicator() // 圆形进度条
                    },
                    error = {
                        Icon(imageVector = Icons.Outlined.Warning, contentDescription = "")
                    },
                    modifier = Modifier.width(200.dp),
                    contentDescription = ""
                )
            }
            Text(text = unit.information)
        }
    }
}

@Serializable
data class GrassItem(
    val information:String,
    val link:String
)