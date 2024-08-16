package com.lollipop.applist.desktop

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lollipop.applist.jadx.JadxTask

@Composable
fun ContentPage(file: JadxTask?) {
    file ?: return
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = file.name ?: "null")
    }
}