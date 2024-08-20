package com.lollipop.applist.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DragBox(
    modifier: Modifier = Modifier.fillMaxSize(),
    showMask: Boolean = false,
    onDropCallback: (DragData) -> Unit,
    dragMask: @Composable BoxScope.() -> Unit = { DragMask() },
    content: @Composable BoxScope.() -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .onExternalDrag(
                onDragStart = {
                    isDragging = true
                },
                onDragExit = {
                    isDragging = false
                },
                onDrag = {

                },
                onDrop = { state ->
                    val dragData = state.dragData
                    onDropCallback(dragData)
                    isDragging = false
                }),
        contentAlignment = Alignment.Center
    ) {
        content()
        if (isDragging || showMask) {
            dragMask()
        }
    }
}

@Composable
fun DragMask(
    modifier: Modifier = Modifier.fillMaxSize()
        .background(Color(255, 255, 255, 160)),
    color: Color = MaterialTheme.colors.primary
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Download,
            contentDescription = "拖拽上传",
            modifier = Modifier.width(56.dp).height(56.dp),
            tint = color
        )
        Text(text = "拖拽上传", fontSize = 18.sp, color = color)
    }
}

