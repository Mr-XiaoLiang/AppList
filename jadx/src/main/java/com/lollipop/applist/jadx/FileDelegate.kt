package com.lollipop.applist.jadx

import java.io.File

class FileDelegate(
    val root: File
) {

    private val resources by lazy {
        File(root, "resources")
    }

    val sources by lazy {
        File(root, "sources")
    }

    val manifest by lazy {
        File(resources, "AndroidManifest.xml")
    }

    val assets by lazy {
        File(resources, "assets")
    }

    val lib by lazy {
        File(resources, "lib")
    }

    val res by lazy {
        File(resources, "res")
    }

}