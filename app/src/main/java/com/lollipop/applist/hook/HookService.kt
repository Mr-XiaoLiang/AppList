package com.lollipop.applist.hook

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.core.app.NotificationCompat
import com.lollipop.applist.R
import com.lollipop.applist.data.AppInfoDatabase

class HookService : AccessibilityService() {

    companion object {

        var isActive: Boolean = false
            private set

        private const val NOTIFICATION_ID = 6666
        private const val NOTIFICATION_CHANNEL_ID = "com.lollipop.applist.ongoing"
        private const val NOTIFICATION_CHANNEL_NAME = "OnGoing Notifications"

        const val ACTION_ACTIVE_STATE_CHANGED =
            "com.lollipop.applist.action.HOOK_ACTIVE_STATE_CHANGED"

        private const val USAGE_STATS_DELAY = 310L

        private const val SP_KEY_ONLY_CHANGED = "save_only_changed"

        fun saveOnlyChanged(context: Context, flag: Boolean) {
            context.getSharedPreferences("AppList", MODE_PRIVATE)
                .edit()
                .putBoolean(SP_KEY_ONLY_CHANGED, flag)
                .apply()
        }

        fun getOnlyChanged(context: Context): Boolean {
            return context.getSharedPreferences("AppList", MODE_PRIVATE)
                .getBoolean(SP_KEY_ONLY_CHANGED, false)
        }

    }

    private val handler = Handler(Looper.getMainLooper())
    private val appInfoDatabase = AppInfoDatabase.Writer()
    private var floatingDelegate: HookFloatingDelegate? = null
    private var isSaveOnlyChanged = false

    private var newPkgTime = 0L
    private var currentAppPackage = ""
    private var currentAppClassName = ""

    override fun onCreate() {
        super.onCreate()
        appInfoDatabase.init(this)
        floatingDelegate = HookFloatingDelegate(this, ::flagApp, ::fetchAppInfoByTouch)
    }

    private fun now(): Long {
        return System.currentTimeMillis()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isSaveOnlyChanged = getOnlyChanged(this)
        newPkgTime = now()
        notifyActive()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "This channel is for ongoing notifications."
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendOngoingNotification() {
        val notificationManager = getNotificationManager()
        createNotificationChannel(notificationManager)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.title_hook_notification_ongoing))
            .setContentText(
                getString(
                    R.string.summary_hook_notification_ongoing,
                    if (isSaveOnlyChanged) {
                        "仅记录变化"
                    } else {
                        "记录全部"
                    }
                )
            )
            .setOngoing(true)
            .setFullScreenIntent(
                PendingIntent.getActivity(
                    this,
                    NOTIFICATION_ID,
                    Intent(this, HookSettingActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ),
                true
            )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun removeOngoingNotification() {
        val notificationManager = getNotificationManager()
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(NotificationManager::class.java)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        notifyInactive()
        return super.onUnbind(intent)
    }

    private fun notifyActive() {
        isActive = true
        sendStateBroadcast()
        sendOngoingNotification()
        floatingDelegate?.show()
    }

    private fun notifyInactive() {
        isActive = false
        sendStateBroadcast()
        removeOngoingNotification()
        floatingDelegate?.hide()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            onWindowStateChanged(event)
        }
    }

    private fun getEventTypeName(type: Int): String {
        when (type) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                return "TYPE_WINDOW_STATE_CHANGED"
            }

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                return "TYPE_WINDOW_CONTENT_CHANGED"
            }

            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                return "TYPE_WINDOWS_CHANGED"
            }

            else -> {
                return type.toString(16)
            }
        }
    }

    private fun onWindowStateChanged(event: AccessibilityEvent) {
        Log.e(
            "AppInfoDatabase",
            "onWindowStateChanged.type: ${getEventTypeName(event.eventType)}"
        )
        fetchAppInfo(0) { pkg, cls ->
            onWindowChanged(pkg, cls)
            if (cls.isEmpty()) {
                fetchAppInfoAgain(USAGE_STATS_DELAY)
            }
        }
    }

    private fun fetchAppInfoByTouch() {
        fetchAppInfoAgain(0)
    }

    private fun fetchAppInfoAgain(delay: Long) {
        Log.e("AppInfoDatabase", "fetchAppInfoAgain")
        fetchAppInfo(delay) { pkg, cls ->
            onWindowChanged(pkg, cls)
        }
    }

    private fun fetchAppInfo(delay: Long, callback: (String, String) -> Unit) {
        handler.postDelayed({
            val root = rootInActiveWindow ?: return@postDelayed
            val packageName = root.packageName?.toString() ?: ""
            var className = ""
            val usageStatsManager = getSystemService(UsageStatsManager::class.java)
            val usageEvents = usageStatsManager?.queryEvents(newPkgTime, now())
            if (usageEvents != null) {
                val usageEvent = UsageEvents.Event()
                var count = 0
                var lastTime = 0L
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(usageEvent)
                    count++
                    if (usageEvent.packageName == packageName && !TextUtils.isEmpty(usageEvent.className)) {
                        if (usageEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED && usageEvent.timeStamp > lastTime) {
                            lastTime = usageEvent.timeStamp
                            className = usageEvent.className
                        }
                    }
                }
                Log.e("AppInfoDatabase", "UsageStatsManager.count: $count")
            }
            callback(packageName, className)
        }, delay)
    }

    private fun onWindowChanged(pkg: String, cls: String) {
        Log.e("AppInfoDatabase", "Source.onWindowChanged: ${pkg}, ${cls}")
        if (pkg != currentAppPackage) {
            newPkgTime = now() - 1000
        }
        if (pkg.isEmpty() || cls.isEmpty()) {
            return
        }
        if (isSaveOnlyChanged) {
            if (pkg == currentAppPackage && cls == currentAppClassName) {
                return
            }
        }
        currentAppPackage = pkg
        currentAppClassName = cls
        appInfoDatabase.onWindowChanged(pkg, cls, false)
        floatingDelegate?.update(pkg, cls)
    }

    private fun flagApp(pkg: String, cls: String): Boolean {
        if (pkg.isEmpty() || cls.isEmpty()) {
            return false
        }
        appInfoDatabase.onWindowChanged(pkg, cls, true)
        return true
    }

    override fun onInterrupt() {
    }

    private fun sendStateBroadcast() {
        sendBroadcast(Intent(ACTION_ACTIVE_STATE_CHANGED))
    }

}