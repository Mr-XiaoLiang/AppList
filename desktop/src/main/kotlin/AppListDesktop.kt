import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import applist.desktop.generated.resources.Res
import applist.desktop.generated.resources.icon_left_panel_close_24
import applist.desktop.generated.resources.icon_left_panel_open_24
import com.lollipop.applist.desktop.DragBox
import com.lollipop.applist.desktop.JadxComposeState
import com.lollipop.applist.desktop.JadxFileHelper
import com.lollipop.applist.desktop.PageContainer
import com.lollipop.applist.desktop.state.AppPlatform
import com.lollipop.applist.desktop.state.UiState
import com.lollipop.applist.desktop.widget.MacAppWindowActionWidget
import com.lollipop.applist.desktop.widget.RoundWindow
import com.lollipop.applist.desktop.widget.WindowsAppWindowActionWidget
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppListDesktop(topInsets: Dp) {
    DragBox(
        modifier = Modifier.fillMaxSize().background(color = Color(240, 240, 240, 255)),
        showMask = false,
        onDropCallback = { data ->
            when (data) {
                is DragData.FilesList -> {
                    val list = data.readFiles()
                    JadxFileHelper.addAllFile(list)
                }
            }
        }
    ) {
        PageContainer(topInsets)
    }
}

private fun log(value: String) {
    println(value)
}


@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    JadxComposeState.init()
    val isMacOS = AppPlatform.isMacOS
    val actionBarHeight = 36.dp
    var dropdownExpanded by remember { UiState.dropdownExpanded }
    var hintExpanded by remember { UiState.hintExpanded }
    val currentFile by remember { JadxComposeState.currentTask }
    val fileList = remember { JadxFileHelper.taskList }
    var menuPanelExpanded by remember { UiState.menuPanelExpanded }
    RoundWindow { windowState ->
        AppListDesktop(topInsets = actionBarHeight)
        WindowDraggableArea(
            modifier = Modifier.fillMaxWidth().height(actionBarHeight)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(36.dp).padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isMacOS) {
                    MacAppWindowActionWidget(windowState = windowState)
                }

                IconButton(onClick = {
                    menuPanelExpanded = !menuPanelExpanded
                }) {
                    if (menuPanelExpanded) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_left_panel_close_24),
                            contentDescription = "收起",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier.width(24.dp).height(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.icon_left_panel_open_24),
                            contentDescription = "展开",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier.width(24.dp).height(24.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        hintExpanded = !hintExpanded
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "帮助",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.width(24.dp).height(24.dp)
                    )
                }
                Text(
                    text = currentFile?.name ?: "",
                    modifier = Modifier.wrapContentSize().weight(1F)
                        .padding(end = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                    color = MaterialTheme.colors.onSurface
                )
                if (fileList.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            dropdownExpanded = !dropdownExpanded
                        }
                    ) {
                        if (dropdownExpanded) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "切换",
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier.width(24.dp).height(24.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "切换",
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier.width(24.dp).height(24.dp)
                            )
                        }
                    }
                }
                IconButton(
                    onClick = {
                        JadxFileHelper.openFileChooser()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "添加",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.width(24.dp).height(24.dp)
                    )
                }
                if (!isMacOS) {
                    WindowsAppWindowActionWidget(windowState = windowState)
                }
            }
        }
    }
}