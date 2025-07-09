package com.lollipop.applist.desktop.widget


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import applist.desktop.generated.resources.*
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun WindowsWindowActionWidget(
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {},
    onMinimize: () -> Unit = {},
    onMaximize: () -> Unit = {},
    isMaximized: Boolean = false,
    closeIcon: Painter = painterResource(Res.drawable.window_action_close_24),
    minimizeIcon: Painter = painterResource(Res.drawable.window_action_minimize_24),
    maximizeIcon: Painter = painterResource(Res.drawable.window_action_maximize_24),
    floatingIcon: Painter? = painterResource(Res.drawable.window_action_floating_24),
    disableColor: Color = Color(0xDD9A9A9A.toInt()),
    closeColor: Color = Color(0xFFE04040.toInt()),
    closeIconColor: Color = Color(0xFF333333.toInt()),
    minimizeColor: Color = Color(0xFFD9C539.toInt()),
    minimizeIconColor: Color = Color(0xFF333333.toInt()),
    maximizeColor: Color = Color(0xFF3AD38D.toInt()),
    maximizeIconColor: Color = Color(0xFF333333.toInt()),
    enableCloseAction: Boolean = true,
    enableMinimizeAction: Boolean = true,
    enableMaximizeAction: Boolean = true,
) {

    val iconSize = 24.dp
    val iconPadding = 4.dp
    val iconInsets = 2.dp

    var isPointerEnter by remember { mutableStateOf(false) }

    val closeBtnColor = if (isPointerEnter && enableCloseAction) {
        closeColor
    } else {
        disableColor
    }
    val closeIconTint = if (isPointerEnter && enableCloseAction) {
        closeIconColor
    } else {
        disableColor
    }

    val minimizeBtnColor = if (isPointerEnter && enableMinimizeAction) {
        minimizeColor
    } else {
        disableColor
    }
    val minimizeIconTint = if (isPointerEnter && enableMinimizeAction) {
        minimizeIconColor
    } else {
        disableColor
    }

    val maximizeBtnColor = if (isPointerEnter && enableMaximizeAction) {
        maximizeColor
    } else {
        disableColor
    }
    val maximizeIconTint = if (isPointerEnter && enableMaximizeAction) {
        maximizeIconColor
    } else {
        disableColor
    }


    Row(
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { isPointerEnter = true }
            .onPointerEvent(PointerEventType.Exit) { isPointerEnter = false },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val maximizeIconPainter = if (isMaximized || floatingIcon == null) {
            maximizeIcon
        } else {
            floatingIcon
        }

        Icon(
            modifier = Modifier.width(iconSize)
                .height(iconSize)
                .onClick(enabled = enableMaximizeAction, onClick = onMaximize)
                .padding(iconPadding)
                .background(color = maximizeBtnColor, shape = RoundedCornerShape(iconSize))
                .padding(iconInsets),
            painter = maximizeIconPainter,
            contentDescription = "Maximize",
            tint = maximizeIconTint
        )

        Icon(
            modifier = Modifier.width(iconSize)
                .height(iconSize)
                .onClick(enabled = enableMinimizeAction, onClick = onMinimize)
                .padding(iconPadding)
                .background(color = minimizeBtnColor, shape = RoundedCornerShape(iconSize))
                .padding(iconInsets),
            painter = minimizeIcon,
            contentDescription = "Minimize",
            tint = minimizeIconTint
        )

        Icon(
            modifier = Modifier.width(iconSize)
                .height(iconSize)
                .onClick(enabled = enableCloseAction, onClick = onExit)
                .padding(iconPadding)
                .background(color = closeBtnColor, shape = RoundedCornerShape(iconSize))
                .padding(iconInsets),
            painter = closeIcon,
            contentDescription = "Close",
            tint = closeIconTint
        )

    }

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MacWindowActionWidget(
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {},
    onMinimize: () -> Unit = {},
    onMaximize: () -> Unit = {},
    isMaximized: Boolean = false,
    closeIcon: Painter = painterResource(Res.drawable.window_action_close_24),
    minimizeIcon: Painter = painterResource(Res.drawable.window_action_minimize_24),
    maximizeIcon: Painter = painterResource(Res.drawable.window_action_maximize_24),
    floatingIcon: Painter? = painterResource(Res.drawable.window_action_floating_24),
    disableColor: Color = Color(0xDD9A9A9A.toInt()),
    closeColor: Color = Color(0xFFE04040.toInt()),
    closeIconColor: Color = Color(0xFF333333.toInt()),
    minimizeColor: Color = Color(0xFFD9C539.toInt()),
    minimizeIconColor: Color = Color(0xFF333333.toInt()),
    maximizeColor: Color = Color(0xFF3AD38D.toInt()),
    maximizeIconColor: Color = Color(0xFF333333.toInt()),
    enableCloseAction: Boolean = true,
    enableMinimizeAction: Boolean = true,
    enableMaximizeAction: Boolean = true,
) {

    val iconSize = 22.dp
    val iconPadding = 4.dp
    val iconInsets = 2.dp

    var isPointerEnter by remember { mutableStateOf(false) }

    val closeBtnColor = if (isPointerEnter && enableCloseAction) {
        closeColor
    } else {
        disableColor
    }
    val closeIconTint = if (isPointerEnter && enableCloseAction) {
        closeIconColor
    } else {
        disableColor
    }

    val minimizeBtnColor = if (isPointerEnter && enableMinimizeAction) {
        minimizeColor
    } else {
        disableColor
    }
    val minimizeIconTint = if (isPointerEnter && enableMinimizeAction) {
        minimizeIconColor
    } else {
        disableColor
    }

    val maximizeBtnColor = if (isPointerEnter && enableMaximizeAction) {
        maximizeColor
    } else {
        disableColor
    }
    val maximizeIconTint = if (isPointerEnter && enableMaximizeAction) {
        maximizeIconColor
    } else {
        disableColor
    }


    Row(
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter) { isPointerEnter = true }
            .onPointerEvent(PointerEventType.Exit) { isPointerEnter = false },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.width(iconSize)
                .height(iconSize)
                .onClick(enabled = enableCloseAction, onClick = onExit)
                .padding(iconPadding)
                .background(color = closeBtnColor, shape = RoundedCornerShape(iconSize))
                .padding(iconInsets),
            painter = closeIcon,
            contentDescription = "Close",
            tint = closeIconTint
        )

        Icon(
            modifier = Modifier.width(iconSize)
                .height(iconSize)
                .onClick(enabled = enableMinimizeAction, onClick = onMinimize)
                .padding(iconPadding)
                .background(color = minimizeBtnColor, shape = RoundedCornerShape(iconSize))
                .padding(iconInsets),
            painter = minimizeIcon,
            contentDescription = "Minimize",
            tint = minimizeIconTint
        )

        val maximizeIconPainter = if (!isMaximized || floatingIcon == null) {
            maximizeIcon
        } else {
            floatingIcon
        }

        Icon(
            modifier = Modifier.width(iconSize)
                .height(iconSize)
                .onClick(enabled = enableMaximizeAction, onClick = onMaximize)
                .padding(iconPadding)
                .background(color = maximizeBtnColor, shape = RoundedCornerShape(iconSize))
                .padding(iconInsets),
            painter = maximizeIconPainter,
            contentDescription = "Maximize",
            tint = maximizeIconTint
        )

    }

}


@Composable
fun ApplicationScope.MacAppWindowActionWidget(
    modifier: Modifier = Modifier,
    windowState: WindowState,
    enableCloseAction: Boolean = true,
    enableMinimizeAction: Boolean = true,
    enableMaximizeAction: Boolean = true,
) {
    var isMaximized by remember {
        mutableStateOf(windowState.placement == WindowPlacement.Maximized)
    }
    MacWindowActionWidget(
        modifier = modifier,
        onExit = {
            exitApplication()
        },
        onMinimize = {
            windowState.isMinimized = true
        },
        onMaximize = {
            if (windowState.placement == WindowPlacement.Maximized) {
                windowState.placement = WindowPlacement.Floating
                isMaximized = false
            } else {
                windowState.placement = WindowPlacement.Maximized
                isMaximized = true
            }
        },
        isMaximized = isMaximized,
        enableCloseAction = enableCloseAction,
        enableMinimizeAction = enableMinimizeAction,
        enableMaximizeAction = enableMaximizeAction,
    )
}

@Composable
fun ApplicationScope.WindowsAppWindowActionWidget(
    modifier: Modifier = Modifier,
    windowState: WindowState,
    enableCloseAction: Boolean = true,
    enableMinimizeAction: Boolean = true,
    enableMaximizeAction: Boolean = true,
) {
    var isMaximized by remember {
        mutableStateOf(windowState.placement == WindowPlacement.Maximized)
    }
    WindowsWindowActionWidget(
        modifier = modifier,
        onExit = {
            exitApplication()
        },
        onMinimize = {
            windowState.isMinimized = true
        },
        onMaximize = {
            if (windowState.placement == WindowPlacement.Maximized) {
                windowState.placement = WindowPlacement.Floating
                isMaximized = false
            } else {
                windowState.placement = WindowPlacement.Maximized
                isMaximized = true
            }
        },
        isMaximized = isMaximized,
        enableCloseAction = enableCloseAction,
        enableMinimizeAction = enableMinimizeAction,
        enableMaximizeAction = enableMaximizeAction,
    )
}