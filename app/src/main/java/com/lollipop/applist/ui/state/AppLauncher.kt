package com.lollipop.applist.ui.state

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.edit
import com.lollipop.applist.data.AppInfo
import com.lollipop.applist.sdk.AppOptionHelper
import com.lollipop.applist.sdk.AppSdkInfoActivity
import com.lollipop.applist.sdk.QuickAppHelper
import java.lang.ref.WeakReference

object AppLauncher {

    private val sourceAppList = ArrayList<AppInfo>()

    val appList = SnapshotStateList<AppInfo>()

    val searchResultList = SnapshotStateList<AppInfo>()
    val quickAppList = SnapshotStateList<AppInfo>()

    val searchValue = mutableStateOf("")

    val isRefreshingState = mutableStateOf(false)

    val isSearchingState = mutableStateOf(false)

    var contextReference: WeakReference<Context>? = null

    private val searchTask = Runnable {
        search()
    }

    fun init(context: Context) {
        this.contextReference = WeakReference(context)
    }

    fun onUi(runnable: Runnable) {
        TaskHelper.onUi(runnable)
    }

    fun doAsync(runnable: Runnable) {
        TaskHelper.doAsync(runnable)
    }

    fun registerApkChooser(activity: ComponentActivity): ActivityResultLauncher<Unit> {
        return activity.registerForActivityResult(ApkChooserContract()) {
            onApkChooserResult(activity, it)
        }
    }

    fun onStart() {
        if (sourceAppList.isEmpty()) {
            loadAppInfo()
        }
    }

    fun showOption(context: Context, info: AppInfo) {
        onUi {
            AppOptionHelper.showOptionDialog(context, info.name.toString(), info.packageName)
        }
    }

    fun openApp(context: Context, info: AppInfo) {
        onUi {
            AppOptionHelper.openApp(context, info.packageName)
        }
    }

    fun loadAppInfo() {
        isRefreshingState.value = true
        isSearchingState.value = true
        val filterSystemApp = isFilterSystemApp()
        doAsync {
            val list = getAppList(filterSystemApp).sortedBy { it.name.toString() }
            onUi {
                isRefreshingState.value = false
                onFullAppListLoaded(list)
            }
        }
    }

    private fun onFullAppListLoaded(list: List<AppInfo>) {
        sourceAppList.clear()
        sourceAppList.addAll(list)
        appList.clear()
        appList.addAll(list)
        QuickAppHelper.loadQuickApp()
        search()
    }

    fun onStop(context: Context) {
        QuickAppHelper.saveQuickApp(context)
        TaskHelper.remove(searchTask)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun search() {
        val text = searchValue.value
        if (text.isEmpty()) {
            searchResultList.clear()
            isSearchingState.value = false
            return
        }
        val lower = text.lowercase()
        searchResultList.clear()
        val result = mutableListOf<AppInfo>()
        for (app in sourceAppList) {
            if (app.lowercaseName.contains(lower) || app.lowercasePackage.contains(lower)) {
                result.add(app)
            }
        }
        searchResultList.addAll(result)
        isSearchingState.value = false
    }

    private fun getAppList(filterSystemApp: Boolean): List<AppInfo> {
        val c = contextReference?.get() ?: return emptyList()
        val manager = c.packageManager
        val applications = manager.getInstalledApplications(PackageManager.GET_ACTIVITIES)
        val resultList = mutableListOf<AppInfo>()
        for (app in applications) {
            if (filterSystemApp && app.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                continue
            }
            resultList.add(
                AppInfo(
                    app.loadLabel(manager),
                    app.packageName,
                    app.loadIcon(manager),
                    app.loadIcon(manager),
                )
            )
        }
        return resultList
    }

    fun updateSearchValue(value: String) {
        searchValue.value = value
        isSearchingState.value = true
        postSearch()
    }

    private fun postSearch() {
        TaskHelper.delay(delay = 100, removePrevious = true, runnable = searchTask)
    }

    fun onQuickAppChange() {
        doAsync {
            val list = ArrayList<AppInfo>()
            appList.forEach {
                if (QuickAppHelper.isQuickApp(it.packageName)) {
                    list.add(it)
                }
            }
            onUi {
                quickAppList.clear()
                quickAppList.addAll(list)
            }
        }
    }

    fun copy(context: Context, value: String) {
        val clipboardManager = context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as? ClipboardManager ?: return
        clipboardManager.setPrimaryClip(ClipData.newPlainText(value, value))
        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
    }

    private fun onApkChooserResult(activity: Activity, result: Uri?) {
        result ?: return
        AppSdkInfoActivity.startByPath(activity, result)
    }

    fun filterSystemApp(enable: Boolean) {
        getPreferences()?.edit { putBoolean("filterSystemApp", enable) }
    }

    fun isFilterSystemApp(): Boolean {
        return getPreferences()?.getBoolean("filterSystemApp", false) ?: false
    }

    private fun getPreferences(): SharedPreferences? {
        return contextReference?.get()?.getSharedPreferences(
            "AppList", Context.MODE_PRIVATE
        )
    }

    private class ApkChooserContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent.createChooser(
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    setType("*/*")
                    addCategory(Intent.CATEGORY_OPENABLE)
                },
                "请选择一个APK文件"
            )
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == RESULT_OK) {
                return intent?.data
            }
            return null
        }

    }

}