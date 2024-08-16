package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
import java.io.File
import java.util.LinkedList

class SourceMenu(
    val root: File
) {

    private val listImpl = mutableListOf<String>()
    private var isParsed = false

    val list: List<String>
        get() {
            return listImpl
        }

    fun parse(out: AppSdkInfo, type: AppSdkInfo.Type) {
        parse()
        for (path in list) {
            out.check(type, path)
        }
    }

    private fun parse() {
        if (isParsed) {
            return
        }
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
                    val relativePath = path.substring(baseName.length + 1).replace("/", ".")
                    listImpl.add(relativePath)
                }
            }
        }
        isParsed = true
    }

}