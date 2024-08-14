import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.applist.desktop.PageContainer


@Composable
fun AppListDesktop() {
    PageContainer()
}

fun main() = application {
    // 先初始化语言
    Window(
        onCloseRequest = ::exitApplication,
        title = "App List",
        icon = painterResource("icon.png")
    ) {
        AppListDesktop()
    }
}