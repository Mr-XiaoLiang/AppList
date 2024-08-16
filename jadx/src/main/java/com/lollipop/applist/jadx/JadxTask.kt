package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
import jadx.api.JadxArgs
import jadx.api.JadxDecompiler
import jadx.api.impl.NoOpCodeCache
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


class JadxTask(
    val file: File,
) {

    companion object {
        private val tempDir by lazy {
//            val property = System.getProperty("java.io.tmpdir")
            val property = System.getProperty("user.home")
            File(property, "AppList")
        }
    }

    val name: String = file.name

    var progressState = -1F
        private set

    var isCompleted = false
        private set

    var isLoading = false
        private set

    private var progressListener: ProgressListener? = null
    private var onCompletedListener: OnCompletedListener? = null

    private val outDir: File by lazy {
        File(tempDir, md5()).apply {
            mkdirs()
            deleteOnExit()
        }
    }

    private val fileDelegate by lazy {
        FileDelegate(outDir)
    }

    val sdkInfo: AppSdkInfo
        get() {
            return fileDelegate.sdkInfo
        }

    val sources: File
        get() {
            return fileDelegate.sources
        }

    val manifest: ManifestParse
        get() {
            return fileDelegate.manifest
        }

    val assets: File
        get() {
            return fileDelegate.assets
        }

    val lib: SourceMenu
        get() {
            return fileDelegate.lib
        }

    val res: File
        get() {
            return fileDelegate.res
        }

    fun reload() {
        if (!isCompleted) {
            return
        }
        fileDelegate.parseSdkInfo()
        onCompletedListener?.onCompleted(this)
    }

    private val saveProgressListener = JadxDecompiler.ProgressListener { done, total ->
        progressState = done * 1F / total
        progressListener?.onProgress(progressState)
    }

    private fun md5(): String {
        val fileName = file.absolutePath
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(fileName.toByteArray())).toString(16).padStart(32, '0')
    }

    fun setProgressListener(listener: ProgressListener?) {
        this.progressListener = listener
        listener?.onProgress(progressState)
    }

    fun setOnCompletedListener(listener: OnCompletedListener?) {
        this.onCompletedListener = listener
    }

    fun load() {
        if (isCompleted) {
            return
        }
        if (isLoading) {
            return
        }
        isLoading = true
        val jadxArgs = JadxArgs()
        jadxArgs.setInputFile(file)
        jadxArgs.codeCache = NoOpCodeCache.INSTANCE
        jadxArgs.outDir = outDir
        try {
            JadxDecompiler(jadxArgs).use { jadx ->
                jadx.load()
                jadx.save(16, saveProgressListener)
            }
            isCompleted = true
            isLoading = false
            reload()
        } catch (e: Exception) {
            isLoading = false
            e.printStackTrace()
        }
    }

    fun interface ProgressListener {
        fun onProgress(progress: Float)
    }

    fun interface OnCompletedListener {
        fun onCompleted(task: JadxTask)
    }

}