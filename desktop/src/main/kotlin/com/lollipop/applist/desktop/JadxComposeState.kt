package com.lollipop.applist.desktop

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.lollipop.applist.jadx.DecompilerMode
import com.lollipop.applist.jadx.JadxTask
import com.lollipop.applist.jadx.JadxTaskManager
import com.lollipop.applist.sdklist.AppSdkInfo
import com.lollipop.applist.sdklist.SdkKeyword

object JadxComposeState {

    val activeTaskProgress = mutableStateOf(-1F)
    private val currentTaskImpl = mutableStateOf<JadxTask?>(null)
    val currentTask: State<JadxTask?>
        get() {
            return currentTaskImpl
        }
    private var isInitialized = false
    val sdkInfoList = mutableStateListOf<AppSdkInfo.Platform>()
    val selectedPlatform = mutableStateOf<SdkKeyword.Sdk?>(null)
    private val platformSourceCodeOriginal = mutableListOf<String>()
    val platformSourceCodeList = mutableStateListOf<String>()
    var sourceCodeFilter = mutableStateOf("")
        private set
    val sdkTypeFilterList = mutableStateMapOf<AppSdkInfo.Type, Boolean>()
    val currentTaskCompleted = mutableStateOf(true)
    var decompilerMode = mutableStateOf(true)

    fun getDecompilerModeEnum(): DecompilerMode {
        return if (decompilerMode.value) {
            DecompilerMode.Runtime
        } else {
            DecompilerMode.File
        }
    }

    fun init() {
        if (isInitialized) {
            return
        }
        JadxTaskManager.activeTaskProgressListener { progress ->
            activeTaskProgress.value = progress
        }
        JadxTaskManager.activeTaskCompletedListener { task ->
            if (task.isCompleted) {
                currentTaskCompleted.value = true
                updateSdkInfoList(task)
            }
        }
        AppSdkInfo.Type.entries.forEach {
            sdkTypeFilterList[it] = AppSdkInfo.typeEnable(it)
        }
        isInitialized = true
    }

    fun selectPlatform(platform: AppSdkInfo.Platform?) {
        if (platform == null) {
            sourceCodeFilter.value = ""
            selectedPlatform.value = null
            platformSourceCodeOriginal.clear()
        } else {
            val oldPlatform = selectedPlatform.value
            if (oldPlatform?.label != platform.sdk.label) {
                // 如果切换平台，则清空过滤器
                sourceCodeFilter.value = ""
            }
            selectedPlatform.value = platform.sdk
            platformSourceCodeOriginal.clear()
            platformSourceCodeOriginal.addAll(platform.source)
        }
        onSourceCodeChanged()
    }

    fun changeSourceCodeFilter(keyword: String) {
        sourceCodeFilter.value = keyword
        onSourceCodeChanged()
    }

    private fun onSourceCodeChanged() {
        platformSourceCodeList.clear()
        platformSourceCodeList.addAll(filterSourceCodeList())
    }

    private fun filterSourceCodeList(): List<String> {
        val source = platformSourceCodeOriginal
        if (source.isEmpty()) {
            return emptyList()
        }
        val keyword = sourceCodeFilter.value
        return source.filter { it.contains(keyword, ignoreCase = true) }
    }

    fun currentTask(task: JadxTask?) {
        currentTaskImpl.value = task
        sdkInfoList.clear()
        JadxTaskManager.activeTask(task)
        currentTaskCompleted.value = task?.isCompleted ?: true
        task ?: return
        if (task.isCompleted) {
            updateSdkInfoList(task)
        }
    }

    fun setSdkTypeEnable(type: AppSdkInfo.Type, enable: Boolean) {
        sdkTypeFilterList[type] = enable
        AppSdkInfo.setTypeFilter(type, enable)
    }

    private fun updateSdkInfoList(task: JadxTask) {
        sdkInfoList.clear()
        sdkInfoList.addAll(task.sdkInfo.getList())
        selectPlatform(null)
    }

}