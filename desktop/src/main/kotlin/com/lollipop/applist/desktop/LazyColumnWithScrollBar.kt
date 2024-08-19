package com.lollipop.applist.desktop

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LazyColumnWithScrollBar(
    modifier: Modifier,
    content: LazyListScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        val scrollState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            content()
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        )
    }
}