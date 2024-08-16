package com.lollipop.applist.desktop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
            LazyColumn(
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
            Button(
                onClick = {
                    task.reload()
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(text = "刷新", color = Color.White, fontSize = 14.sp)
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(
                if (sourceCodeList.isNotEmpty()) {
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
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(sdkInfoList) { index, platform ->
                    if (platform.list.isNotEmpty() || platform.source.isNotEmpty()) {
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
                                .onClick {
                                    JadxComposeState.selectPlatform(platform)
                                }
                        ) {
                            Text(
                                text = platform.sdk.label,
                                color = if (platform.sdk == selectedPlatform) {
                                    MaterialTheme.colors.primary
                                } else {
                                    Color(0xFF333333.toInt())
                                },
                                fontSize = 22.sp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            platform.list.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
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
                                    Text(
                                        text = item.value,
                                        color = Color(0xFF666666.toInt()),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (sourceCodeList.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    .padding(end = 4.dp, top = 4.dp, bottom = 4.dp, start = 2.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(sourceCodeList) { index, clazz ->
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