package com.lollipop.applist.desktop

import java.io.File

object FileHelper {

    fun writeFile(file: File, content: String) {
        file.parentFile.mkdirs()
        file.writeText(content)
    }

}