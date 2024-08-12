package com.lollipop.applist

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lollipop.applist.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), QuickAppHelper.OnQuickAppChangeListener {

    private val appList = ArrayList<AppInfo>()
    private val displayList = ArrayList<AppInfo>()
    private val quickAppList = ArrayList<AppInfo>()

    private val adapter = AppAdapter(displayList)
    private val quickAdapter = QuickAppAdapter(quickAppList, ::onQuickAppClick)

    private val taskHandler = Handler(Looper.getMainLooper())

    private var searchValue = ""

    private val searchTask = Runnable {
        search()
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val executor = Executors.newCachedThreadPool()

    private val apkChooser by lazy {
        registerForActivityResult(ApkChooserContract()) {
            onApkChooserResult(it)
        }
    }

    private val launcherPanelHelper by lazy {
        LauncherContentHelper(binding.launcherContentView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initView()
        initLauncher()
        QuickAppHelper.addOnQuickAppChangeListener(this, this)
        // 调用一下，让它实例化
        apkChooser
    }

    private fun initLauncher() {
        launcherPanelHelper
    }

    private fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.launcherContentView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.hintButton) { v, insets ->
            val dp16 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                resources.displayMetrics
            ).toInt()
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<MarginLayoutParams> {
                leftMargin = dp16 + systemBars.left
                topMargin = dp16 + systemBars.top
                rightMargin = dp16 + systemBars.right
                bottomMargin = dp16 + systemBars.bottom
            }
            insets
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.quickList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.quickList.adapter = quickAdapter

        binding.searchInputView.doOnTextChanged { text, start, before, count ->
            searchValue = text?.toString()?.trim() ?: ""
            binding.clearButton.isVisible = searchValue.isNotEmpty()
            postSearch()
        }

        binding.clearButton.setOnClickListener {
            binding.searchInputView.setText("")
        }

        binding.swipeRefreshLayout.setColorSchemeColors(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadAppInfo()
        }

        binding.hintButton.setOnClickListener {
            showHintDialog()
        }

        binding.quickList.isVisible = false

        binding.menuButton.setOnClickListener {
            if (binding.slidingPaneLayout.isOpen) {
                binding.slidingPaneLayout.close()
            } else {
                binding.slidingPaneLayout.open()
            }
        }

    }

    private fun showHintDialog() {
        val filterSystemApp = isFilterSystemApp()
        val menuList = OptionMenu.entries.filter {
            when (it) {
                OptionMenu.FILTER_SYSTEM_APP -> !filterSystemApp
                OptionMenu.PASS_SYSTEM_APP -> filterSystemApp
                else -> true
            }
        }
        val menuNameList = menuList.map { it.label }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setItems(menuNameList) { dialog, which ->
                doOption(menuList[which])
                dialog.dismiss()
            }
            .show()
    }

    override fun onStart() {
        super.onStart()
        if (appList.isEmpty()) {
            loadAppInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.slidingPaneLayout.post {
            val density = resources.displayMetrics.density
            val dpWidth = binding.slidingPaneLayout.width / density
            binding.menuButton.isVisible = dpWidth < 600f
        }
    }

    private fun onQuickAppClick(info: AppInfo) {
        runOnUiThread {
            AppOptionHelper.showOptionDialog(this, info.name.toString(), info.packageName)
        }
    }

    private fun loadAppInfo() {
        binding.swipeRefreshLayout.isRefreshing = true
        val filterSystemApp = isFilterSystemApp()
        executor.execute {
            val list = getAppList(filterSystemApp).sortedBy { it.name.toString() }
            runOnUiThread {
                binding.swipeRefreshLayout.isRefreshing = false
                onFullAppListLoaded(list)
            }
        }
    }

    private fun onFullAppListLoaded(list: List<AppInfo>) {
        appList.clear()
        appList.addAll(list)
        QuickAppHelper.loadQuickApp(this)
        search()
        launcherPanelHelper.updateAppList(list)
    }

    override fun onStop() {
        super.onStop()
        QuickAppHelper.saveQuickApp(this)
        taskHandler.removeCallbacks(searchTask)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun search() {
        val text = searchValue
        if (text.isEmpty()) {
            displayList.clear()
            displayList.addAll(appList)
            adapter.notifyDataSetChanged()
            return
        }
        val lower = text.lowercase()
        displayList.clear()
        for (app in appList) {
            if (app.lowercaseName.contains(lower) || app.lowercasePackage.contains(lower)) {
                displayList.add(app)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun getAppList(filterSystemApp: Boolean): List<AppInfo> {
        val manager = packageManager
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

    private fun postSearch() {
        taskHandler.removeCallbacks(searchTask)
        taskHandler.postDelayed(searchTask, 100)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onQuickAppChange() {
        executor.execute {
            val list = ArrayList<AppInfo>()
            appList.forEach {
                if (QuickAppHelper.isQuickApp(it.packageName)) {
                    list.add(it)
                }
            }
            runOnUiThread {
                quickAppList.clear()
                quickAppList.addAll(list)
                quickAdapter.notifyDataSetChanged()
                binding.quickList.isVisible = quickAppList.isNotEmpty()
            }
        }
    }

    private fun doOption(optionMenu: OptionMenu) {
        when (optionMenu) {
            OptionMenu.LOAD_APK -> {
                chooserFile()
            }

            OptionMenu.PM_LIST -> {
                copy(this, optionMenu.label)
            }

            OptionMenu.PM_PATH -> {
                copy(this, optionMenu.label)
            }

            OptionMenu.FILTER_SYSTEM_APP -> {
                filterSystemApp(true)
                loadAppInfo()
            }

            OptionMenu.PASS_SYSTEM_APP -> {
                filterSystemApp(false)
                loadAppInfo()
            }
        }
    }

    private fun copy(context: Context, value: String) {
        val clipboardManager = context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as? ClipboardManager ?: return
        clipboardManager.setPrimaryClip(ClipData.newPlainText(value, value))
        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
    }

    private enum class OptionMenu(val label: String) {
        PM_LIST("adb shell pm list package"),
        PM_PATH("adb shell pm path [package]"),
        LOAD_APK("解析APK"),
        FILTER_SYSTEM_APP("过滤系统应用"),
        PASS_SYSTEM_APP("保留系统应用"),
    }

    private fun chooserFile() {
        apkChooser.launch(Unit)
    }

    private fun onApkChooserResult(result: Uri?) {
        result ?: return
        AppSdkInfoActivity.startByPath(this, result)
    }

    private fun filterSystemApp(enable: Boolean) {
        getPreferences().edit().putBoolean("filterSystemApp", enable).apply()
    }

    private fun isFilterSystemApp(): Boolean {
        return getPreferences().getBoolean("filterSystemApp", false)
    }

    private fun getPreferences(): SharedPreferences {
        return getSharedPreferences("AppList", Context.MODE_PRIVATE)
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