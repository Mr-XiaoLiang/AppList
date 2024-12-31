package com.lollipop.applist.hook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.applist.R
import com.lollipop.applist.data.AppInfoDatabase
import com.lollipop.applist.data.InfoSaveHelper
import com.lollipop.applist.databinding.ActivityAppPageHistoryBinding
import com.lollipop.applist.databinding.ItemAppPageBinding
import com.lollipop.applist.helper.LoadMoreHelper
import java.text.SimpleDateFormat
import java.util.Locale

class AppPageHistoryActivity : AppCompatActivity() {

    companion object {

        private const val EXTRA_PACKAGE_NAME = "packageName"
        private const val EXTRA_APP_NAME = "appName"

        fun start(context: Context, packageName: String, appName: String) {
            val intent = Intent(context, AppPageHistoryActivity::class.java)
            intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
            intent.putExtra(EXTRA_APP_NAME, appName)
            context.startActivity(intent)
        }
    }

    private val binding by lazy {
        ActivityAppPageHistoryBinding.inflate(layoutInflater)
    }

    private val loadMoreHelper by lazy {
        LoadMoreHelper(onLoadMore = ::loadMore)
    }

    private val appPackage by lazy {
        intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
    }

    private val appName by lazy {
        intent.getStringExtra(EXTRA_APP_NAME) ?: ""
    }

    private val appInfoDatabase = AppInfoDatabase.Reader()

    private var pageIndex = 0

    private val dataList = ArrayList<AppInfoDatabase.AppActivityInfo>()

    private val adapter = InfoAdapter(dataList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initInsets()
        binding.actionBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.actionBar.setOnMenuItemClickListener {
            onOptionsItemClick(it)
        }
        binding.actionBar.title = appName
        appInfoDatabase.init(this)
        binding.swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        loadMoreHelper.bind(binding.recyclerView)
        onRefresh()
    }

    private fun onRefresh() {
        pageIndex = 0
        loadInfo()
    }

    private fun loadMore() {
        pageIndex++
        loadInfo()
    }

    private fun onOptionsItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
            }

            R.id.menu_save -> {
                saveInfo()
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    private fun saveInfo() {
        appInfoDatabase.queryAll(appPackage) { list ->
            InfoSaveHelper.save(
                context = this,
                name = appName,
                infoProvider = {
                    contentToString(list)
                },
                onEnd = {
                    Toast.makeText(this, getString(R.string.save_success, it), Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }

    /**
     * 耗时操作，需要在子线程执行
     */
    private fun contentToString(list: List<AppInfoDatabase.AppActivityInfo>): String {
        val builder = StringBuilder()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault())
        for (info in list) {
            builder.append(sdf.format(info.timeDate)).append(" ")
                .append(info.packageName)

            if (info.flag) {
                builder.append(" F ")
            } else {
                builder.append("   ")
            }
            builder.append(info.activityName).append("\n")
        }
        return builder.toString()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadInfo() {
        val index = pageIndex
        appInfoDatabase.query(appPackage, index) { result ->
            if (index == 0) {
                dataList.clear()
                dataList.addAll(result)
                adapter.notifyDataSetChanged()
            } else {
                val start = dataList.size
                dataList.addAll(result)
                adapter.notifyItemRangeInserted(start, result.size)
            }
            onLoadEnd()
        }
    }

    private fun onLoadEnd() {
        binding.swipeRefreshLayout.isRefreshing = false
        loadMoreHelper.enable = true
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        binding.recyclerView.clipToPadding = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private class InfoAdapter(
        private val list: List<AppInfoDatabase.AppActivityInfo>
    ) : RecyclerView.Adapter<InfoHolder>() {

        private var layoutInflater: LayoutInflater? = null

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflater ?: LayoutInflater.from(parent.context).also {
                layoutInflater = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoHolder {
            return InfoHolder(ItemAppPageBinding.inflate(getLayoutInflater(parent), parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: InfoHolder, position: Int) {
            holder.bind(list[position])
        }
    }

    private class InfoHolder(
        private val binding: ItemAppPageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val sdf by lazy {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault())
        }

        fun bind(info: AppInfoDatabase.AppActivityInfo) {
            binding.flagIcon.isVisible = info.flag
            binding.timeView.text = sdf.format(info.timeDate)
            binding.pkgView.text = info.packageName
            binding.labelView.text = info.activityName
        }

    }

}