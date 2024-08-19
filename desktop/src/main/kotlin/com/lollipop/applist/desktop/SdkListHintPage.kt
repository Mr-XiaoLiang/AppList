package com.lollipop.applist.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lollipop.applist.sdklist.SdkKeyword


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SdkListHintPage(expanded: Boolean, onDismissRequest: () -> Unit) {
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
                    itemsIndexed(SdkKeyword.sdkLists) { index, sdk ->
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
                            Text(
                                text = sdk.label,
                                color = Color(0xFF333333.toInt()),
                                fontSize = 22.sp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            Text(
                                text = sdk.keywordsString,
                                color = Color(0xFF666666.toInt()),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize().onClick { onDismissRequest() })
        }
    }
}