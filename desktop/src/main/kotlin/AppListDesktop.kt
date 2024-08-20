import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.applist.desktop.DragBox
import com.lollipop.applist.desktop.JadxComposeState
import com.lollipop.applist.desktop.PageContainer
import com.lollipop.applist.jadx.DecompilerMode
import com.lollipop.applist.jadx.JadxTask
import com.lollipop.applist.jadx.JadxTaskManager
import java.io.File
import java.util.LinkedList


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppListDesktop() {
    val fileList = remember { mutableStateListOf<JadxTask>() }
    DragBox(
        modifier = Modifier.fillMaxSize().background(color = Color(240, 240, 240, 255)),
        showMask = false,
        onDropCallback = { data ->
            when (data) {
                is DragData.FilesList -> {
                    val list = data.readFiles()
                    val mode = JadxComposeState.getDecompilerModeEnum()
                    list.forEach { path ->
                        log("DragData.FilesList：${path}, mode: $mode")
                        fileList.addAllFile(path, mode)
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

private fun MutableList<JadxTask>.addAllFile(path: String, mode: DecompilerMode) {
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
        add(createTask(file, mode))
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
            add(createTask(first, mode))
            log("添加文件：${file.path}")
        }
    }
}

private fun createTask(file: File, mode: DecompilerMode): JadxTask {
    return JadxTask(file, mode).apply {
        JadxTaskManager.addTask(this)
    }
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