package com.lollipop.applist.desktop

import androidx.compose.runtime.mutableStateListOf
import com.lollipop.applist.jadx.DecompilerMode
import com.lollipop.applist.jadx.JadxTask
import com.lollipop.applist.jadx.JadxTaskManager
import java.io.File
import java.util.*
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter


object JadxFileHelper {

    const val FILE_CHOOSER_DIR = "fileChooserDir"

    val taskList = mutableStateListOf<JadxTask>()

    private var lastOpenDir: File? = null

    private fun fileChooserDir(): File {
        val last = lastOpenDir
        if (last != null) {
            if (last.exists()) {
                return last
            }
        }
        resumeFileChooserDir()?.let {
            return it
        }
        val homeDir = File(System.getProperty("user.home"))
        if (homeDir.exists()) {
            return homeDir
        }
        return File(".")
    }

    private fun rememberFileChooserDir(dir: File) {
        lastOpenDir = dir
        PreferencesHelper[FILE_CHOOSER_DIR] = dir.path
    }

    private fun resumeFileChooserDir(): File? {
        PreferencesHelper[FILE_CHOOSER_DIR]?.let { path ->
            if (path.isNotEmpty()) {
                val dir = File(path)
                if (dir.exists()) {
                    lastOpenDir = dir
                    return dir
                }
            }
        }
        return null
    }

    fun openFileChooser() {
        val currentDir = fileChooserDir()
        val chooser = JFileChooser()
        chooser.setCurrentDirectory(currentDir)
        chooser.setDialogTitle("文件选择")
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
        chooser.setFileFilter(ApkFileFilter())

        // 禁用“所有文件”选项
        chooser.setAcceptAllFileFilterUsed(false)

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            chooser.currentDirectory?.let {
                rememberFileChooserDir(it)
            }
            val files = chooser.selectedFiles?.map { it.path }
            if (files != null && files.isNotEmpty()) {
                addAllFile(files)
                return
            }
            val file = chooser.selectedFile
            if (file != null && file.exists()) {
                addAllFile(file.path)
            }
        }
    }

    fun addAllFile(pathList: List<String>) {
        val mode = JadxComposeState.getDecompilerModeEnum()
        pathList.forEach { path ->
            log("DragData.FilesList：${path}, mode: $mode")
            taskList.addAllFile(path, mode)
        }
    }

    fun addAllFile(path: String) {
        addAllFile(listOf(path))
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

    private fun log(value: String) {
        println(value)
    }

    class ApkFileFilter : FileFilter() {

        override fun accept(pathname: File?): Boolean {
            pathname ?: return false
            if (pathname.isDirectory) {
                return true
            }
            val name = pathname.name.lowercase()
            if (name.endsWith(".apk")) {
                return true
            }
            if (name.endsWith(".jar")) {
                return true
            }
            if (name.endsWith(".aab")) {
                return true
            }
            if (name.endsWith(".aar")) {
                return true
            }
            return false
        }

        override fun getDescription(): String {
            return "APK、JAR、AAB、AAR"
        }

    }

}