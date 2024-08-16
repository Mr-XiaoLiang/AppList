package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
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

    val sdkInfo by lazy {
        AppSdkInfo()
    }

    fun parseSdkInfo() {
        sdkInfo.clear()
        sdkInfo.setSelfPackageName(manifest.pkgName)
        manifest.parse(sdkInfo)
        sourceJava.parse(sdkInfo, AppSdkInfo.Type.SourceCode)
        lib.parse(sdkInfo, AppSdkInfo.Type.Native)
    }

    val manifestFile by lazy {
        File(resources, "AndroidManifest.xml")
    }

    val manifest by lazy {
        ManifestParse(manifestFile)
    }

    val sourceJava by lazy {
        SourceMenu(sources)
    }

    val assets by lazy {
        File(resources, "assets")
    }

    val lib by lazy {
        SourceMenu(File(resources, "lib"))
    }

    val res by lazy {
        File(resources, "res")
    }

}