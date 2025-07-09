package com.lollipop.applist.sdk

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.lollipop.applist.ui.state.AppLauncher
import org.json.JSONArray
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object QuickAppHelper {

    private const val TAG = "QuickAppHelper"
    private const val QUICK_APP_FILE_NAME = "quick_app.json"

    private val quickAppSet = ConcurrentHashMap<String, String>()

    private var quickAppFile: File? = null

    private val mainThread by lazy {
        Handler(Looper.getMainLooper())
    }

    private val ioThreadImpl by lazy {
        val thread = HandlerThread("QuickAppIO")
        thread.start()
        thread
    }

    private val ioThread by lazy {
        Handler(ioThreadImpl.looper)
    }

    private val notifyTask = Runnable {
        try {
            AppLauncher.onQuickAppChange()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private val saveTask = Runnable {
        val file = quickAppFile
        if (file != null) {
            val jsonArray = JSONArray()
            quickAppSet.keys.forEach {
                jsonArray.put(it)
            }
            file.writeText(jsonArray.toString())
        }
    }

    fun init(context: Context) {
        rememberQuickAppFile(context)
    }

    private fun rememberQuickAppFile(context: Context): File {
        val file = File(context.dataDir, QUICK_APP_FILE_NAME)
        quickAppFile = file
        return file
    }

    private fun postNotify() {
        mainThread.removeCallbacks(notifyTask)
        mainThread.postDelayed(notifyTask, 10)
    }

    fun addQuickApp(pkgName: String) {
        quickAppSet[pkgName] = TAG
        postNotify()
    }

    fun removeQuickApp(pkgName: String) {
        quickAppSet.remove(pkgName)
        postNotify()
    }

    fun isQuickApp(pkgName: String): Boolean {
        return quickAppSet.containsKey(pkgName)
    }

    fun saveQuickApp(context: Context? = null) {
        if (context != null) {
            rememberQuickAppFile(context)
        }
        ioThread.removeCallbacks(saveTask)
        ioThread.postDelayed(saveTask, 100)
    }

    fun loadQuickApp(context: Context? = null) {
        val file = if (context != null) {
            rememberQuickAppFile(context)
        } else {
            quickAppFile
        }
        if (file == null || !file.exists()) {
            return
        }
        ioThread.post {
            val jsonArray = JSONArray(file.readText())
            for (i in 0 until jsonArray.length()) {
                val pkgName = jsonArray.getString(i)
                quickAppSet[pkgName] = TAG
            }
            postNotify()
        }
    }

}