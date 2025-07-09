package com.lollipop.applist.desktop.state

import androidx.compose.runtime.mutableStateOf

object UiState {

    val hintExpanded = mutableStateOf(false)

    val dropdownExpanded = mutableStateOf(false)

    val menuPanelExpanded = mutableStateOf(true)

}