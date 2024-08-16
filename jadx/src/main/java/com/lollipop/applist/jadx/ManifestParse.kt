package com.lollipop.applist.jadx

import java.io.File

class ManifestParse(private val file: File) {

    val manifestSrc by lazy {
        file.readText()
    }

}