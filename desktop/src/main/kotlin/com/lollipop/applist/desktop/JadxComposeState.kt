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
    val platformSourceCodeList = mutableStateListOf<String>()
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
        selectedPlatform.value = platform?.sdk
        platformSourceCodeList.clear()
        platform?.source?.let {
            platformSourceCodeList.addAll(it)
        }
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