package com.lollipop.applist.ui.state

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors

object TaskHelper {

    private val mainHandler = Handler(Looper.getMainLooper())

    private val executor by lazy {
        Executors.newCachedThreadPool()
    }

    fun remove(runnable: Runnable) {
        mainHandler.removeCallbacks(runnable)
    }

    fun delay(delay: Long, removePrevious: Boolean = true, runnable: Runnable) {
        if (removePrevious) {
            mainHandler.removeCallbacks(runnable)
        }
        mainHandler.postDelayed(runnable, delay)
    }

    fun onUi(runnable: Runnable) {
        mainHandler.post {
            try {
                runnable.run()
            } catch (e: Throwable) {
                Log.e("AppLauncher", "onUi", e)
            }
        }
    }

    fun doAsync(runnable: Runnable) {
        executor.execute {
            try {
                runnable.run()
            } catch (e: Throwable) {
                Log.e("AppLauncher", "doAsync", e)
            }
        }
    }

}