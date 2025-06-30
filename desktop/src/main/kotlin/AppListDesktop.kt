import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.applist.desktop.DragBox
import com.lollipop.applist.desktop.JadxComposeState
import com.lollipop.applist.desktop.JadxFileHelper
import com.lollipop.applist.desktop.PageContainer


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppListDesktop() {
    val fileList = remember { JadxFileHelper.taskList }
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
        PageContainer(fileList) { f ->
            fileList.remove(f)
        }
    }
}

private fun log(value: String) {
    println(value)
}


fun main() = application {
    JadxComposeState.init()
    Window(
        onCloseRequest = ::exitApplication,
        title = "App List",
        icon = painterResource("icon.png")
    ) {
        AppListDesktop()
    }
}