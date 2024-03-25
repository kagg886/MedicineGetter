package com.kagg886.medicine_getter.ui.screen.tool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kagg886.medicine_getter.LocalNavController

@Composable
fun ToolScreen() {
    Column {
        ToolItem(name = "病历单识别", desc = "识别出病历单中的注意事项等", jumpToRoute = "Tool_OCR")
        Divider()
    }
}

@Composable
fun ToolItem(
    name: String,
    desc: String,
    jumpToRoute: String,
) {
    val nav = LocalNavController.current
    ListItem(modifier = Modifier.clickable {
        nav.navigate(jumpToRoute)
    }, headlineContent = {
        Text(text = name)
    }, supportingContent = {
        Text(text = desc)
    })
}