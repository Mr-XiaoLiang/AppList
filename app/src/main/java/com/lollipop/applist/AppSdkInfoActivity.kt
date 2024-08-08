package com.lollipop.applist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.applist.databinding.ActivitySdkInfoBinding
import java.io.File
import java.io.FileOutputStream

class AppSdkInfoActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {

        private const val PARAMS_PACKAGE_NAME = "PACKAGE_NAME"

        fun startByPackage(context: Context, packageName: String) {
            start(context) {
                it.putExtra(PARAMS_PACKAGE_NAME, packageName)
            }
        }

        fun startByPath(context: Context, packagePath: Uri) {
            start(context) {
                it.data = packagePath
            }
        }

        private fun start(context: Context, builder: (Intent) -> Unit) {
            context.startActivity(Intent(context, AppSdkInfoActivity::class.java).apply {
                builder(this)
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
    private var packagePath: Uri? = null
    private var appLabel: CharSequence = ""

    private val displayHelper = AppSdkDisplayHelper.create()
    private val sdkInfo = AppSdkInfo()

    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initView()
        packageName = intent.getStringExtra(PARAMS_PACKAGE_NAME) ?: ""
        packagePath = intent.data
        updateTitle()
        onRefresh()
    }

    private fun updateTitle() {
        setTitle(appLabel.ifEmpty { packageName })
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
        setSupportActionBar(binding.actionBar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
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
        if (isLoading) {
            return
        }
        binding.swipeRefreshLayout.isRefreshing = true
        isLoading = true
        Thread {
            val start = System.currentTimeMillis()
            getAppInfoSync()
            val end = System.currentTimeMillis()
            val l = end - start
            onUI {
                updateTitle()
                displayHelper.update(sdkInfo.getList())
                binding.swipeRefreshLayout.isRefreshing = false
                isLoading = false
                Toast.makeText(this, "耗时: ${l}ms", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun onUI(callback: () -> Unit) {
        runOnUiThread {
            callback()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sdk_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
            }

            R.id.menu_save -> {
                saveInfo()
            }

            R.id.menu_info -> {

            }

            R.id.menu_filter -> {

            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    private fun saveInfo() {
        InfoSaveHelper.save(
            context = this,
            name = getSaveFileName(),
            infoProvider = {
                sdkInfo.toJson().toString(4)
            },
            onEnd = {
                Toast.makeText(this, getString(R.string.save_success, it), Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }

    private fun getAppInfoSync() {
        sdkInfo.clear()

        var pmFlag = 0
        pmFlag = pmFlag or PackageManager.GET_ACTIVITIES
        pmFlag = pmFlag or PackageManager.GET_SERVICES
        pmFlag = pmFlag or PackageManager.GET_PROVIDERS
        pmFlag = pmFlag or PackageManager.GET_META_DATA
        pmFlag = pmFlag or PackageManager.GET_RECEIVERS
        pmFlag = pmFlag or PackageManager.GET_PERMISSIONS
        val manager = packageManager
        val packageInfo = getPackageInfo(pmFlag)
        sdkInfo.setSelfPackageName(packageInfo.packageName ?: "")
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
        packageInfo.applicationInfo?.let { applicationInfo ->
            appLabel = applicationInfo.loadLabel(manager)

            applicationInfo.metaData?.let { metaData ->
                metaData.keySet().forEach { key ->
                    val value = "$key = ${metaData.get(key)}"
                    sdkInfo.check(AppSdkInfo.Type.MetaData, value)
                }
            }

            applicationInfo.nativeLibraryDir?.let { nativeLibraryDir ->
                File(nativeLibraryDir).list()?.forEach { fileName ->
                    sdkInfo.check(AppSdkInfo.Type.Native, fileName)
                }
            }

        }

        sdkInfo.app.let { app ->
            app.packageName = packageInfo.packageName ?: ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                app.versionCode = packageInfo.longVersionCode.toString()
            } else {
                app.versionCode = packageInfo.versionCode.toString()
            }
            app.versionName = packageInfo.versionName ?: ""
            app.label = appLabel.toString()
        }
    }

    private fun getPackageInfo(flags: Int): PackageInfo {
        var resultInfo: PackageInfo? = null
        val manager = packageManager ?: return PackageInfo()
        if (packageName.isNotEmpty()) {
            try {
                resultInfo = manager.getPackageInfo(packageName, flags)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        val localUri = packagePath
        val localPath = localUri?.path ?: ""
        if (resultInfo == null && localPath.isNotEmpty()) {
            try {
                resultInfo = manager.getPackageArchiveInfo(localPath, flags)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        if (resultInfo == null && localUri != null) {
            try {
                val tempApk = File(cacheDir, "temp.apk")
                if (tempApk.exists()) {
                    tempApk.delete()
                }
                contentResolver.openInputStream(localUri).use { input ->
                    val output = FileOutputStream(tempApk)
                    input?.copyTo(output)
                }
                resultInfo = manager.getPackageArchiveInfo(tempApk.absolutePath, flags)
                packagePath = Uri.parse(tempApk.absolutePath)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return resultInfo ?: PackageInfo()
    }

}
