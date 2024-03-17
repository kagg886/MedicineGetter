package com.kagg886.medicine_getter.ui.screen.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import com.kagg886.medicine_getter.BuildConfig
import com.kagg886.medicine_getter.network.AIResult

@Composable
fun GrassListItem(item: AIResult) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            SubcomposeAsyncImage(
                model = "${BuildConfig.AI_HOST}/ai/image?spell=${item.spell}",
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    } // 圆形进度条
                },
                error = {
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = "")
                },
                modifier = Modifier.clickable {
                                              showDialog = false
                },
                contentDescription = ""
            )
        }
    }
    ListItem(headlineContent = {
        Text(text = item.name)
    }, leadingContent = {
        SubcomposeAsyncImage(
            model = "${BuildConfig.AI_HOST}/ai/image?spell=${item.spell}",
            loading = {
                CircularProgressIndicator() // 圆形进度条
            },
            error = {
                Icon(imageVector = Icons.Outlined.Warning, contentDescription = "")
            },
            modifier = Modifier
                .width(40.dp)
                .clickable {
                    showDialog = true
                },
            contentDescription = ""
        )
    }, supportingContent = {
        Text(text = "识别率:${item.rate}")
    }, trailingContent = {
        IconButton(onClick = {
            TODO("实现百科详情栏目")
        }) {
            Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = "")
        }
    })

}

@Preview
@Composable
fun GrassListItemPreview() {
    GrassListItem(item = AIResult("安息香", 0.999f, "Anxixiang"))
}