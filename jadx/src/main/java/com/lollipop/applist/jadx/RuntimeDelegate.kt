package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
import jadx.api.JadxDecompiler
import jadx.api.JavaClass
import jadx.api.ResourceFile
import jadx.api.ResourceType
import jadx.core.xmlgen.ResContainer

class RuntimeDelegate : JadxInfoDelegate {

    private val classList = mutableListOf<JavaClass>()
    private val resourceList = mutableListOf<ResourceFile>()
    private val libList = mutableListOf<String>()

    private var manifestInfo = ""

    override val sdkInfo by lazy {
        AppSdkInfo()
    }

    override val manifest by lazy {
        ManifestParse.FromString {
            getManifestValue()
        }
    }

    private val sourceJava by lazy {
        SourceMenu.FromList {
            getSourceList()
        }
    }

    private val lib by lazy {
        SourceMenu.FromList {
            getLibList()
        }
    }

    private fun getSourceList(): List<String> {
        return classList.map { it.fullName }
    }

    private fun getLibList(): List<String> {
        return libList
    }

    private fun getManifestValue(): String {
        return manifestInfo
    }

    private fun loadResourceInfo() {
        for (res in resourceList) {
            try {
                if (res.type == ResourceType.MANIFEST) {
                    val loadContent = res.loadContent()
                    if (loadContent.dataType == ResContainer.DataType.TEXT) {
                        manifestInfo = loadContent.text.codeStr
                    }
                } else if (res.type == ResourceType.LIB) {
                    libList.add(res.originalName)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
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
        classList.clear()
        classList.addAll(jadx.classes)
        resourceList.clear()
        resourceList.addAll(jadx.resources)
        manifestInfo = ""
        loadResourceInfo()
        progressListener.progress(100, 100)
        println("resources count = ${jadx.resources.size}")
        println("classes count = ${jadx.classes.size}")
    }
}