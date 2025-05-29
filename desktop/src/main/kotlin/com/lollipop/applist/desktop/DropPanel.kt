package com.lollipop.applist.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.graphics.Color
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
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    true
                },
                target = object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        val dragData = event.dragData()
                        onDropCallback(dragData)
                        isDragging = false
                        return true
                    }
                    override fun onExited(event: DragAndDropEvent) {
                        isDragging = false
                        println("DragBox.onExited")
                    }
                    override fun onEntered(event: DragAndDropEvent) {
                        isDragging = true
                        println("DragBox.onEntered")
                    }
                }
            ),
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

