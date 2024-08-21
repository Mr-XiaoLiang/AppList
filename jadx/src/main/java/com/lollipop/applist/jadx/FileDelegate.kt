package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
import jadx.api.JadxDecompiler
import java.io.File

class FileDelegate(
    val root: File
) : JadxInfoDelegate {

    private val resources by lazy {
        File(root, "resources")
    }

    val sources by lazy {
        File(root, "sources")
    }

    override val sdkInfo by lazy {
        AppSdkInfo()
    }

    override fun parseSdkInfo() {
        sdkInfo.clear()
        manifest.reload()
        sourceJava.reload()
        lib.reload()

        sdkInfo.setSelfPackageName(manifest.pkgName)
        manifest.parse(sdkInfo)
        sourceJava.parse(sdkInfo, AppSdkInfo.Type.SourceCode)
        lib.parse(sdkInfo, AppSdkInfo.Type.Native)
    }

    override fun reset(jadx: JadxDecompiler, progressListener: JadxDecompiler.ProgressListener) {
        jadx.save(100, progressListener)
    }

    val manifestFile by lazy {
        File(resources, "AndroidManifest.xml")
    }

    override val manifest: ManifestParse by lazy {
        ManifestParse.FromFile(manifestFile)
    }

    val sourceJava by lazy {
        SourceMenu.FromFile(sources)
    }

    val assets by lazy {
        File(resources, "assets")
    }

    val lib by lazy {
        SourceMenu.FromFile(File(resources, "lib"))
    }

    val res by lazy {
        File(resources, "res")
    }

}