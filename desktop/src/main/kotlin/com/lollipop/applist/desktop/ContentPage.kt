package com.lollipop.applist.desktop

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.applist.desktop.state.UiState
import com.lollipop.applist.sdklist.AppSdkInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentPage() {
    val fileList = remember { JadxFileHelper.taskList }
    val sdkInfoList = remember { JadxComposeState.sdkInfoList }
    val currentFile by remember { JadxComposeState.currentTask }
    val sourceCodeList = remember { JadxComposeState.platformSourceCodeList }
    val sdkTypeFilterList = remember { JadxComposeState.sdkTypeFilterList }
    val selectedPlatform by remember { JadxComposeState.selectedPlatform }
    val sourceCodeFilter by remember { JadxComposeState.sourceCodeFilter }
    val saveLoading by remember { JadxComposeState.saveLoading }
    var decompilerMode by remember { JadxComposeState.decompilerMode }
    var menuPanelExpanded by remember { UiState.menuPanelExpanded }
    val menuPanelWidth by animateDpAsState(
        targetValue = if (menuPanelExpanded) {
            240.dp
        } else {
            0.dp
        }
    )
    val contentBoxWeight by animateFloatAsState(
        targetValue = if (currentFile != null && selectedPlatform != null) {
            0.5F
        } else {
            1F
        }
    )
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp)
    ) {
        Column(
            modifier = Modifier.width(menuPanelWidth).wrapContentHeight()
                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumnWithScrollBar(
                modifier = Modifier.fillMaxWidth().weight(1F)
            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (decompilerMode) {
                                "内存模式"
                            } else {
                                "导出模式"
                            },
                            fontSize = 14.sp,
                            modifier = Modifier.wrapContentSize().padding(end = 4.dp),
                            color = MaterialTheme.colors.onSurface,
                        )
                        Spacer(modifier = Modifier.weight(1F))
                        Switch(
                            checked = decompilerMode,
                            onCheckedChange = {
                                decompilerMode = it
                            },
                            modifier = Modifier.wrapContentWidth().height(36.dp)
                        )
                    }
                }

                items(AppSdkInfo.Type.entries) { type ->
                    val isChecked = sdkTypeFilterList[type] ?: false
                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            .padding(horizontal = 8.dp, vertical = 4.dp).onClick {
                                JadxComposeState.setSdkTypeEnable(type, !isChecked)
                            },
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = type.display, fontSize = 14.sp)
                        Spacer(modifier = Modifier.weight(1F))
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                JadxComposeState.setSdkTypeEnable(type, checked)
                            }
                        )
                    }
                }
            }
            if (currentFile != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            currentFile?.reload()
                        },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.width(24.dp).height(24.dp),
                                tint = Color.White
                            )
                            Text(text = "刷新", color = Color.White, fontSize = 14.sp)
                        }
                    }
                    Button(
                        onClick = {
                            if (!saveLoading) {
                                JadxComposeState.saveCurrentSdkInfo()
                            }
                        },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (saveLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(24.dp).height(24.dp).padding(2.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    strokeCap = StrokeCap.Round,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Save,
                                    contentDescription = "Save",
                                    modifier = Modifier.width(24.dp).height(24.dp),
                                    tint = Color.White
                                )
                            }
                            Text(text = "保存", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.animateContentSize()
                .fillMaxWidth(contentBoxWeight)
                .fillMaxHeight()
                .padding(end = 2.dp, top = 4.dp, bottom = 4.dp, start = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.White)
        ) {
            if (fileList.isEmpty()) {
                DragMask(
                    clickable = true,
                    color = Color(0, 0, 0, 80)
                )
            } else {
                LazyColumnWithScrollBar(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(sdkInfoList) { index, platform ->
                        if (platform.list.isNotEmpty() || platform.source.isNotEmpty()) {
                            val isSelectedPlatform = platform.sdk == selectedPlatform
                            Column(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .background(
                                        color = if (index % 2 == 0) {
                                            Color.White
                                        } else {
                                            Color(250, 250, 250, 255)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ).padding(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = platform.sdk.typeName,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                            .background(
                                                color = Color(platform.sdk.color),
                                                shape = RoundedCornerShape(4.dp)
                                            ).padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                    SelectionContainer(
                                        modifier = Modifier.weight(1F)
                                    ) {
                                        Text(
                                            text = platform.sdk.label,
                                            color = if (isSelectedPlatform) {
                                                MaterialTheme.colors.primary
                                            } else {
                                                Color(0xFF333333.toInt())
                                            },
                                            fontSize = 22.sp,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                    if (platform.sdk.website.isNotEmpty()) {
                                        IconButton(onClick = {
                                            JadxComposeState.openWebsite(platform.sdk.website)
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Language,
                                                contentDescription = "官网",
                                                modifier = Modifier.width(24.dp).height(24.dp),
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                    IconButton(onClick = {
                                        if (isSelectedPlatform) {
                                            JadxComposeState.selectPlatform(null)
                                        } else {
                                            JadxComposeState.selectPlatform(platform)
                                        }
                                    }) {
                                        if (isSelectedPlatform) {
                                            Icon(
                                                imageVector = Icons.Filled.ChevronRight,
                                                contentDescription = "折叠",
                                                modifier = Modifier.width(24.dp).height(24.dp),
                                                tint = Color.Gray
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Filled.ChevronLeft,
                                                contentDescription = "展开",
                                                modifier = Modifier.width(24.dp).height(24.dp),
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }
                                platform.list.forEach { item ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(start = 24.dp)
                                    ) {
                                        Text(
                                            text = item.type.label,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 4.dp,
                                                vertical = 2.dp
                                            )
                                                .background(
                                                    color = Color(item.type.color),
                                                    shape = RoundedCornerShape(4.dp)
                                                ).padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                        SelectionContainer(
                                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                        ) {
                                            Text(
                                                text = item.value,
                                                color = Color(0xFF666666.toInt()),
                                                fontSize = 14.sp,
                                                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (selectedPlatform != null) {
            val platform = selectedPlatform?.label ?: ""
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    .padding(end = 4.dp, top = 4.dp, bottom = 4.dp, start = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = Color.White)
            ) {
                OutlinedTextField(
                    leadingIcon = {
                        Icon(Icons.Filled.Search, null)
                    },
                    label = {
                        Text(text = "过滤: $platform")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    value = sourceCodeFilter,
                    onValueChange = {
                        JadxComposeState.changeSourceCodeFilter(it)
                    }
                )
                LazyColumnWithScrollBar(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(sourceCodeList) { index, clazz ->
                        SelectionContainer {
                            Text(
                                text = clazz,
                                color = Color(0xFF666666.toInt()),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}