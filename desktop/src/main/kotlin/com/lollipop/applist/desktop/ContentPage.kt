package com.lollipop.applist.desktop

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.applist.jadx.JadxTask
import com.lollipop.applist.sdklist.AppSdkInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentPage(task: JadxTask?) {
    task ?: return
    val sdkInfoList = remember { JadxComposeState.sdkInfoList }
    val sourceCodeList = remember { JadxComposeState.platformSourceCodeList }
    val sdkTypeFilterList = remember { JadxComposeState.sdkTypeFilterList }
    val selectedPlatform by remember { JadxComposeState.selectedPlatform }
    val sourceCodeFilter by remember { JadxComposeState.sourceCodeFilter }
    val saveLoading by remember { JadxComposeState.saveLoading }
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp)
    ) {
        Column(
            modifier = Modifier.width(200.dp).wrapContentHeight()
                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 2.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumnWithScrollBar(
                modifier = Modifier.fillMaxWidth().weight(1F)
            ) {
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
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                JadxComposeState.setSdkTypeEnable(type, checked)
                            }
                        )
                        Text(text = type.label, fontSize = 14.sp)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        task.reload()
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
        Box(
            modifier = Modifier.animateContentSize().fillMaxWidth(
                if (sourceCodeList.isNotEmpty() || sourceCodeFilter.isNotEmpty()) {
                    0.5F
                } else {
                    1F
                }
            ).fillMaxHeight()
                .padding(end = 2.dp, top = 4.dp, bottom = 4.dp, start = 2.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
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
                                            imageVector = Icons.Filled.UnfoldLess,
                                            contentDescription = "折叠",
                                            modifier = Modifier.width(24.dp).height(24.dp),
                                            tint = Color.Gray
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.UnfoldMore,
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
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                .padding(end = 4.dp, top = 4.dp, bottom = 4.dp, start = 2.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(Icons.Filled.Search, null)
                },
                label = {
                    Text(text = "过滤")
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