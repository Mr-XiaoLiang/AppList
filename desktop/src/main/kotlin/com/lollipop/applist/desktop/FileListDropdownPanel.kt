package com.lollipop.applist.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.lollipop.applist.desktop.state.UiState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListDropdownPanel() {
    var dropdownExpanded by remember { UiState.dropdownExpanded }
    val currentFile by remember { JadxComposeState.currentTask }
    val fileList = remember { JadxFileHelper.taskList }

    AnimatedVisibility(
        visible = dropdownExpanded,
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
                val currentName = currentFile?.name
                LazyColumnWithScrollBar(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(fileList) { file ->
                        val fileName = file.name
                        val isCurrent = currentName == fileName
                        val color = if (isCurrent) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onSecondary
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .border(
                                    width = 1.dp,
                                    shape = RoundedCornerShape(8.dp),
                                    color = color
                                )
                                .padding(horizontal = 8.dp)
                                .onClick { JadxComposeState.currentTask(file) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fileName,
                                modifier = Modifier.weight(1F).padding(start = 4.dp),
                                color = color
                            )
                            IconButton(
                                onClick = {
                                    JadxComposeState.removeTask(file)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "关闭标签",
                                    tint = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.width(24.dp)
                                        .height(24.dp)
                                )
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize().onClick { dropdownExpanded = false })
        }
    }
}
