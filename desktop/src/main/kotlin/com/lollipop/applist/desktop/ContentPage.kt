package com.lollipop.applist.desktop

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.io.File

@Composable
fun ContentPage(file: File?) {
    file?:return
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = file.name ?: "null")
    }
}