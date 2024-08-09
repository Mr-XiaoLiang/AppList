package com.lollipop.applist

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.json.JSONArray
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

object QuickAppHelper {

    private const val TAG = "QuickAppHelper"
    private const val QUICK_APP_FILE_NAME = "quick_app.json"

    private val observerList = ArrayList<OnQuickAppChangeListener>()

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
            observerList.forEach {
                try {
                    it.onQuickAppChange()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
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

    fun saveQuickApp(context: Context) {
        rememberQuickAppFile(context)
        ioThread.removeCallbacks(saveTask)
        ioThread.postDelayed(saveTask, 100)
    }

    fun loadQuickApp(context: Context) {
        val file = rememberQuickAppFile(context)
        if (!file.exists()) {
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

    fun interface OnQuickAppChangeListener {
        fun onQuickAppChange()
    }

    fun addOnQuickAppChangeListener(listener: OnQuickAppChangeListener) {
        observerList.add(listener)
    }

    fun removeOnQuickAppChangeListener(listener: OnQuickAppChangeListener) {
        observerList.remove(listener)
    }

    fun addOnQuickAppChangeListener(source: LifecycleOwner, listener: OnQuickAppChangeListener) {
        addOnQuickAppChangeListener(LifecycleObserver(source, listener))
    }

    class LifecycleObserver(
        source: LifecycleOwner,
        private val impl: OnQuickAppChangeListener
    ) : LifecycleEventObserver, OnQuickAppChangeListener {

        private var currentState: Lifecycle.State = source.lifecycle.currentState
        private var lifecycleObserver: WeakReference<LifecycleOwner> = WeakReference(source)
        private var pendingUpdate = false

        init {
            source.lifecycle.addObserver(this)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            currentState = event.targetState
            if (lifecycleObserver.get() !== source) {
                lifecycleObserver.get()?.lifecycle?.removeObserver(this)
                source.lifecycle.addObserver(this)
                lifecycleObserver = WeakReference(source)
            }
            if (event == Lifecycle.Event.ON_RESUME && pendingUpdate) {
                invokeCallback()
            }
        }

        private fun invokeCallback() {
            impl.onQuickAppChange()
            pendingUpdate = false
        }

        override fun onQuickAppChange() {
            when (currentState) {
                Lifecycle.State.DESTROYED -> {
                    lifecycleObserver.get()?.lifecycle?.removeObserver(this)
                    lifecycleObserver.clear()
                    removeOnQuickAppChangeListener(this)
                }

                Lifecycle.State.INITIALIZED -> {
                    pendingUpdate = true
                }

                Lifecycle.State.CREATED -> {
                    pendingUpdate = true
                }

                Lifecycle.State.STARTED -> {
                    pendingUpdate = true
                }

                Lifecycle.State.RESUMED -> {
                    invokeCallback()
                }
            }
        }
    }

}