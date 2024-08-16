package com.lollipop.applist.jadx

import java.util.concurrent.Executors


object JadxTaskManager {

    private val taskMap = mutableMapOf<String, JadxTask>()

    private var activeTask: JadxTask? = null

    private val executor by lazy {
        Executors.newCachedThreadPool()
    }

    var currentTaskProgress: Float = 0F
        private set

    private var activeTaskProgressOutListener: ActiveTaskProgressListener? = null

    private val activeTaskProgressListener = JadxTask.ProgressListener {
        currentTaskProgress = it
        activeTaskProgressOutListener?.onProgress(it)
    }

    private fun getTaskKey(task: JadxTask): String {
        return task.file.absolutePath
    }

    fun addTask(task: JadxTask) {
        val taskKey = getTaskKey(task)
        if (taskMap.containsKey(taskKey)) {
            return
        }
        taskMap[taskKey] = task
        startTask(task)
    }

    fun activeTaskProgressListener(listener: ActiveTaskProgressListener?) {
        activeTaskProgressOutListener = listener
    }

    fun activeTask(task: JadxTask?) {
        activeTask?.setProgressListener(null)
        activeTask = task
        task?.setProgressListener(activeTaskProgressListener)
    }

    fun removeTask(task: JadxTask) {
        val taskKey = getTaskKey(task)
        taskMap.remove(taskKey)
    }

    private fun startTask(task: JadxTask) {
        if (task.isLoading || task.isCompleted) {
            return
        }
        executor.execute {
            try {
                task.load()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun interface ActiveTaskProgressListener {
        fun onProgress(progress: Float)
    }

}