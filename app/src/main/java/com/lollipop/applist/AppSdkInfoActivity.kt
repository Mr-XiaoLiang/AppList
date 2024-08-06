package com.lollipop.applist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.applist.databinding.ActivitySdkInfoBinding

class AppSdkInfoActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {

        private const val PARAMS_PACKAGE_NAME = "PACKAGE_NAME"

        fun start(context: Context, packageName: String) {
            context.startActivity(Intent(context, AppSdkInfoActivity::class.java).apply {
                putExtra(PARAMS_PACKAGE_NAME, packageName)
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            })
        }
    }

    private val binding by lazy {
        ActivitySdkInfoBinding.inflate(layoutInflater)
    }

    private var packageName = ""
    private var appLabel: CharSequence = ""

    private val displayHelper = AppSdkDisplayHelper.create()
    private val sdkInfo = AppSdkInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initView()
        packageName = intent.getStringExtra(PARAMS_PACKAGE_NAME) ?: ""
        updateTitle()
        onRefresh()
    }

    private fun updateTitle() {
        binding.titleView.post {
            binding.titleView.text = appLabel.ifEmpty { packageName }
        }
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
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.saveButton.setOnClickListener {
            InfoSaveHelper.save(
                context = this,
                name = getSaveFileName(),
                infoProvider = {
                    sdkInfo.toJson().toString(4)
                },
                onEnd = {
                    Toast.makeText(this, "保存完成: $it", Toast.LENGTH_SHORT).show()
                }
            )
        }
        binding.swipeRefreshLayout.setColorSchemeColors(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW
        )
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        displayHelper.attach(binding.recyclerView)
    }

    private fun getSaveFileName(): String {
        var result = ""
        val label = appLabel.toString()
        if (label.isNotEmpty()) {
            result = label.replace("\\s".toRegex(), "_")
        }
        if (result.isEmpty()) {
            result = packageName
        }
        return result
    }

    override fun onRefresh() {
        getAppInfo()
        displayHelper.update(sdkInfo.getList())
    }

    private fun getAppInfo() {
        val start = System.currentTimeMillis()
        sdkInfo.clear()
        val manager = packageManager
        var pmFlag = 0
        pmFlag = pmFlag or PackageManager.GET_ACTIVITIES
        pmFlag = pmFlag or PackageManager.GET_SERVICES
        pmFlag = pmFlag or PackageManager.GET_PROVIDERS
        pmFlag = pmFlag or PackageManager.GET_META_DATA
        pmFlag = pmFlag or PackageManager.GET_RECEIVERS
        pmFlag = pmFlag or PackageManager.GET_PERMISSIONS
        val packageInfo = manager.getPackageInfo(packageName, pmFlag)

        packageInfo.activities?.forEach {
            sdkInfo.check(AppSdkInfo.Type.Activity, it.name)
        }
        packageInfo.services?.forEach {
            sdkInfo.check(AppSdkInfo.Type.Service, it.name)
        }
        packageInfo.providers?.forEach {
            sdkInfo.check(AppSdkInfo.Type.Provider, it.name)
        }
        packageInfo.permissions?.forEach {
            sdkInfo.check(AppSdkInfo.Type.Permission, it.name)
        }
        packageInfo.requestedPermissions?.forEach {
            sdkInfo.check(AppSdkInfo.Type.Permission, it)
        }
        packageInfo.receivers?.forEach {
            sdkInfo.check(AppSdkInfo.Type.Receiver, it.name)
        }
        val applicationInfo = packageInfo.applicationInfo

        appLabel = applicationInfo.loadLabel(manager)
        updateTitle()

        applicationInfo.metaData?.let { metaData ->
            metaData.keySet().forEach { key ->
                val value = "$key = ${metaData.get(key)}"
                sdkInfo.check(AppSdkInfo.Type.MetaData, value)
            }
        }
        val end = System.currentTimeMillis()
        val l = end - start
        Toast.makeText(this, "耗时: ${l}ms", Toast.LENGTH_SHORT).show()
    }

}
