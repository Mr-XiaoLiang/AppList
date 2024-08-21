package com.lollipop.applist.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lollipop.applist.jadx.JadxTask

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageContainer(
    fileList: List<JadxTask>,
    callClose: (JadxTask) -> Unit
) {

    val currentFile by remember { JadxComposeState.currentTask }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var hintExpanded by remember { mutableStateOf(false) }
    val currentProgress by remember { JadxComposeState.activeTaskProgress }
    val currentTaskCompleted by remember { JadxComposeState.currentTaskCompleted }
    var decompilerMode by remember { JadxComposeState.decompilerMode }

    val closeFileCallback = { task: JadxTask ->
        callClose(task)
        if (currentFile == task) {
            JadxComposeState.currentTask(null)
        }
    }

    if (fileList.isEmpty()) {
        dropdownExpanded = false
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (currentFile == null) {
            if (fileList.isNotEmpty()) {
                JadxComposeState.currentTask(fileList[0])
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(36.dp).onClick {
                dropdownExpanded = !dropdownExpanded
            }.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "帮助",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.width(36.dp).height(36.dp).onClick {
                    hintExpanded = !hintExpanded
                }.padding(6.dp)
            )
            Text(
                text = currentFile?.name ?: "",
                modifier = Modifier.wrapContentSize().weight(1F)
                    .padding(end = 4.dp),
                color = MaterialTheme.colors.onSurface
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "切换",
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.width(24.dp).height(24.dp)
            )
            Switch(
                checked = decompilerMode,
                onCheckedChange = {
                    decompilerMode = it
                },
                modifier = Modifier.wrapContentWidth().height(36.dp)
            )
            Text(
                text = if (decompilerMode) {
                    "内存模式"
                } else {
                    "导出模式"
                },
                modifier = Modifier.wrapContentSize().padding(end = 4.dp),
                color = MaterialTheme.colors.onSurface
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (fileList.isNotEmpty()) {
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
                ContentPage(currentFile)
            } else {
                DragMask(color = Color(0, 0, 0, 80))
            }
            DropdownPanel(
                fileList = fileList,
                expanded = dropdownExpanded,
                currentName = currentFile?.name ?: "",
                onDismissRequest = { dropdownExpanded = false },
                callClose = closeFileCallback
            ) { file ->
                JadxComposeState.currentTask(file)
                dropdownExpanded = false
            }
            SdkListHintPage(hintExpanded) { hintExpanded = false }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DropdownPanel(
    fileList: List<JadxTask>,
    expanded: Boolean,
    currentName: String,
    onDismissRequest: () -> Unit,
    callClose: (JadxTask) -> Unit,
    onClick: (JadxTask) -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6F),
                elevation = 8.dp,
                backgroundColor = Color.White,
                shape = MaterialTheme.shapes.large
            ) {
                LazyColumnWithScrollBar(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(fileList) { file ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .onClick {
                                    onClick(file)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "关闭标签",
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier.width(16.dp).height(16.dp).onClick {
                                    callClose(file)
                                }
                            )
                            val fileName = file.name
                            Text(
                                text = fileName,
                                modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                                color = if (currentName == fileName) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.onSurface
                                }
                            )
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize().onClick { onDismissRequest() })
        }
    }
}
