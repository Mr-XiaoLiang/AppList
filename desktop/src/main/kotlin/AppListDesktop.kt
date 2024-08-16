import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.applist.desktop.DragBox
import com.lollipop.applist.desktop.JadxComposeState
import com.lollipop.applist.desktop.PageContainer
import com.lollipop.applist.jadx.JadxTask
import com.lollipop.applist.jadx.JadxTaskManager
import java.io.File
import java.util.LinkedList


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppListDesktop() {
    val fileList = remember { mutableStateListOf<JadxTask>() }
    DragBox(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colors.surface),
        showMask = fileList.isEmpty(),
        onDropCallback = { data ->
            when (data) {
                is DragData.FilesList -> {
                    val list = data.readFiles()
                    list.forEach { path ->
                        log("DragData.FilesList：${path}")
                        fileList.addAllFile(path)
                    }
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

private fun MutableList<JadxTask>.addAllFile(path: String) {
    val realPath = if (path.startsWith("file:")) {
        path.substring(5)
    } else {
        path
    }
    val file = File(realPath)
    if (!file.exists()) {
        log("文件不存在：${file.path}")
        return
    }
    if (file.isFile) {
        add(createTask(file))
        log("添加文件：${file.path}")
        return
    }
    val pendingList = LinkedList<File>()
    pendingList.addLast(file)
    while (pendingList.isNotEmpty()) {
        val first = pendingList.removeFirst()
        if (!first.exists()) {
            continue
        }
        if (first.isDirectory) {
            val files = first.listFiles() ?: continue
            files.forEach { f ->
                pendingList.addLast(f)
                log("遍历目录：${f.path}")
            }
        } else if (first.isFile) {
            add(createTask(first))
            log("添加文件：${file.path}")
        }
    }
}

private fun createTask(file: File): JadxTask {
    return JadxTask(file).apply {
        JadxTaskManager.addTask(this)

    }
}

fun main() = application {
    JadxComposeState.init()
    // 先初始化语言
    Window(
        onCloseRequest = ::exitApplication,
        title = "App List",
        icon = painterResource("icon.png")
    ) {
        AppListDesktop()
    }
}