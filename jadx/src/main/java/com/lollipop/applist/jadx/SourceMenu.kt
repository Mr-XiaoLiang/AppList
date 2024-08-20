package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
import java.io.File
import java.util.LinkedList

sealed class SourceMenu {

    protected val listImpl = mutableListOf<String>()
    private var isParsed = false
    val list: List<String>
        get() {
            return listImpl
        }

    fun parse(out: AppSdkInfo, type: AppSdkInfo.Type) {
        loadInfo()
        for (path in list) {
            out.check(type, path)
        }
    }

    fun reload() {
        isParsed = false
        listImpl.clear()
        loadInfo()
    }

    private fun loadInfo() {
        if (isParsed) {
            return
        }
        tryLoad()
        isParsed = true
    }

    protected abstract fun tryLoad()

    class FromFile(private val root: File) : SourceMenu() {

        override fun tryLoad() {
            val baseName = root.absolutePath
            val fileList = LinkedList<File>()
            fileList.addLast(root)
            while (fileList.isNotEmpty()) {
                val first = fileList.removeFirst()
                if (first.isDirectory) {
                    first.listFiles()?.forEach {
                        fileList.addLast(it)
                    }
                } else if (first.isFile) {
                    val path = first.absolutePath
                    if (path.startsWith(baseName)) {
                        val relativePath = path.substring(baseName.length + 1)
                            .replace("/", ".")
                            .replace("\\", ".")
                        listImpl.add(relativePath)
                    }
                }
            }
        }
    }

    class FromList(private val dataProvider: () -> List<String>) : SourceMenu() {

        override fun tryLoad() {
            listImpl.clear()
            listImpl.addAll(dataProvider())
        }

    }

}