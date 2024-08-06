package com.lollipop.applist

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initView()
        QuickAppHelper.addOnQuickAppChangeListener(this, this)
    }

    private fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
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
            v.updateLayoutParams<FrameLayout.LayoutParams> {
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
            MaterialAlertDialogBuilder(this)
                .setMessage(
                    """
                    adb shell pm list package
                    adb shell pm path [package name]
                """.trimIndent()
                )
                .show()
        }

        binding.quickList.isVisible = false

    }

    override fun onStart() {
        super.onStart()
        loadAppInfo()
    }

    private fun onQuickAppClick(pkgName: String) {
        runOnUiThread {
            binding.searchInputView.setText(pkgName)
        }
    }

    private fun loadAppInfo() {
        binding.swipeRefreshLayout.isRefreshing = true
        executor.execute {
            val list = getAppList().sortedBy { it.name.toString() }
            runOnUiThread {
                binding.swipeRefreshLayout.isRefreshing = false
                appList.clear()
                appList.addAll(list)
                QuickAppHelper.loadQuickApp(this)
                search()
            }
        }
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

    private fun getAppList(): List<AppInfo> {
        val manager = packageManager
        val applications = manager.getInstalledApplications(PackageManager.GET_ACTIVITIES)
        return applications.map {
            AppInfo(
                it.loadLabel(manager),
                it.packageName,
                it.loadIcon(manager)
            )
        }
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

}