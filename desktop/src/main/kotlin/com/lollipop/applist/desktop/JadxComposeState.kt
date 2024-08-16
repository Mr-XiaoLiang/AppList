package com.lollipop.applist.desktop

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.lollipop.applist.jadx.JadxTask
import com.lollipop.applist.jadx.JadxTaskManager

object JadxComposeState {

    val activeTaskProgress = mutableStateOf(0F)
    private val currentTaskImpl = mutableStateOf<JadxTask?>(null)
    val currentTask: State<JadxTask?>
        get() {
            return currentTaskImpl
        }

    private var isInitialized = false

    fun init() {
        if (isInitialized) {
            return
        }
        JadxTaskManager.activeTaskProgressListener { progress ->
            activeTaskProgress.value = progress
        }
        isInitialized = true
    }

    fun currentTask(task: JadxTask?) {
        currentTaskImpl.value = task
        JadxTaskManager.activeTask(task)
    }

}