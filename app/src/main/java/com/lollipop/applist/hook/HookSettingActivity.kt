package com.lollipop.applist.hook

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lollipop.applist.databinding.ActivityHookSettingBinding

class HookSettingActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHookSettingBinding.inflate(layoutInflater)
    }

    private val broadcastDelegate = HookBroadcastDelegate {
        updateState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initInsets()
        binding.accessibilityCard.setOnClickListener {
            // 打开无障碍设置页面
            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
        binding.actionBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.usageCard.setOnClickListener {
            // 打开使用统计设置页面
            val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
        binding.onlyChangeSwitch.setOnCheckedChangeListener { _, isChecked ->
            HookService.saveOnlyChanged(this, isChecked)
        }
        binding.notificationCard.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                    putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
                }
            } else {
                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    setData(Uri.fromParts("package", packageName, null))
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateState()
        broadcastDelegate.register(this)
    }

    override fun onPause() {
        super.onPause()
        broadcastDelegate.unregister(this)
    }

    private fun updateState() {
        if (lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
            binding.accessibilitySwitch.isChecked = HookService.isActive
            binding.usageSwitch.isChecked = isUsageStateEnabled()
            binding.onlyChangeSwitch.isChecked = HookService.getOnlyChanged(this)
            binding.notificationSwitch.isChecked =
                NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    private fun isUsageStateEnabled(): Boolean {
        val usageStatsManager = getSystemService(UsageStatsManager::class.java) ?: return false
        val now = System.currentTimeMillis()
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - 1000,
            now
        ).isNotEmpty()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentGroup) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private class HookBroadcastDelegate(
        private val onActiveStateChanged: () -> Unit
    ) : BroadcastReceiver() {

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        fun register(context: Context) {
            val intentFilter = IntentFilter(HookService.ACTION_ACTIVE_STATE_CHANGED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(
                    this,
                    intentFilter,
                    Context.RECEIVER_EXPORTED
                )
            } else {
                context.registerReceiver(
                    this,
                    intentFilter
                )
            }
        }

        fun unregister(context: Context) {
            context.unregisterReceiver(this)
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            intent ?: return
            when (intent.action) {
                HookService.ACTION_ACTIVE_STATE_CHANGED -> {
                    onActiveStateChanged()
                }
            }
        }
    }

}