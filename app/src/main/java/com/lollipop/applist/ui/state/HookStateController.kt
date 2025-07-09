package com.lollipop.applist.ui.state

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import com.lollipop.applist.ComposeMainActivity
import com.lollipop.applist.hook.HookService
import java.lang.ref.WeakReference

object HookStateController {

    val isAccessibilityEnable = mutableStateOf(false)

    val isUsageStateEnable = mutableStateOf(false)

    val isNotificationEnable = mutableStateOf(false)

    val isOnlyChangedLog = mutableStateOf(false)

    val viewFilterInterval = mutableIntStateOf(1)

    fun init(context: Context) {
        Settings.init(context)
    }

    fun onResume(context: Context) {
        isAccessibilityEnable.value = HookService.isActive
        isUsageStateEnable.value = isUsageStateEnabled(context)
        isOnlyChangedLog.value = Settings.getOnlyChanged(context)
        isNotificationEnable.value =
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        viewFilterInterval.intValue = Settings.getViewFilterInterval(context)
    }

    fun settingsActivity(context: Context): Intent {
        return Intent(context, ComposeMainActivity::class.java)
    }

    private fun isUsageStateEnabled(context: Context): Boolean {
        val usageStatsManager =
            context.getSystemService(UsageStatsManager::class.java) ?: return false
        val now = System.currentTimeMillis()
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - 1000,
            now
        ).isNotEmpty()
    }

    fun setOnlyChangedLog(context: Context, value: Boolean) {
        Settings.saveOnlyChanged(context, value)
        isOnlyChangedLog.value = value
    }

    fun updateViewFilterInterval(value: Int) {
        viewFilterInterval.intValue = value
        Settings.postSaveViewFilterInterval()
    }

    object Settings {
        private const val KEY_SP = "AppList"

        private const val SP_KEY_ONLY_CHANGED = "save_only_changed"

        private const val SP_KEY_VIEW_FILTER_INTERVAL = "view_filter_interval"

        private var contextReference: WeakReference<Context>? = null

        fun init(context: Context) {
            if (contextReference?.get() == context) {
                return
            }
            this.contextReference = WeakReference(context)
        }

        private val saveViewFilterIntervalTask = Runnable {
            saveViewFilterIntervalImpl()
        }

        fun postSaveViewFilterInterval() {
            TaskHelper.delay(
                delay = 500,
                removePrevious = true,
                runnable = saveViewFilterIntervalTask
            )
        }

        fun saveOnlyChanged(context: Context? = null, flag: Boolean) {
            context?.let { init(it) }
            getPreferences()?.edit { putBoolean(SP_KEY_ONLY_CHANGED, flag) }
        }

        fun getOnlyChanged(context: Context? = null): Boolean {
            context?.let { init(it) }
            return getPreferences()?.getBoolean(SP_KEY_ONLY_CHANGED, false) ?: false
        }

        private fun saveViewFilterIntervalImpl() {
            val interval = viewFilterInterval.intValue
            getPreferences()?.edit {
                putInt(SP_KEY_VIEW_FILTER_INTERVAL, interval)
            }
        }

        fun getViewFilterInterval(context: Context? = null): Int {
            context?.let { init(it) }
            return getPreferences()?.getInt(
                SP_KEY_VIEW_FILTER_INTERVAL, 1
            )?.coerceAtLeast(1) ?: 1
        }

        private fun getPreferences(): SharedPreferences? {
            return contextReference?.get()?.getSharedPreferences(
                KEY_SP, MODE_PRIVATE
            )
        }

    }

}