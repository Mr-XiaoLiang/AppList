package com.lollipop.applist.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lollipop.applist.desktop.state.UiState
import com.lollipop.applist.jadx.JadxTask

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageContainer(topInsets: Dp) {
    val fileList = remember { JadxFileHelper.taskList }
    val currentFile by remember { JadxComposeState.currentTask }
    val currentProgress by remember { JadxComposeState.activeTaskProgress }
    val currentTaskCompleted by remember { JadxComposeState.currentTaskCompleted }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = topInsets)
    ) {
        if (currentFile == null) {
            if (fileList.isNotEmpty()) {
                JadxComposeState.currentTask(fileList[0])
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!currentTaskCompleted) {
                if (currentProgress >= 0) {
                    LinearProgressIndicator(
                        progress = currentProgress,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            ContentPage()
            FileListDropdownPanel()
            SdkListHintPage()
        }
    }
}
