package com.lollipop.applist.hook

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lollipop.applist.data.AppInfoDatabase
import com.lollipop.applist.data.InfoSaveHelper
import com.lollipop.applist.sdklist.CsvHelper
import java.text.SimpleDateFormat
import java.util.Locale

class AppPageSaveHelper(
    private val context: Context,
    private val appInfoDatabase: AppInfoDatabase.Reader,
    private val appPackage: String,
    private val appName: String,
) {

    private val sdf by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault())
    }

    fun save(onEnd: OnSaveEnd) {
        val menuNameList = SaveFormat.entries.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle("$appName 保存为")
            .setItems(menuNameList) { dialog, which ->
                save(SaveFormat.entries[which], onEnd)
                dialog.dismiss()
            }
            .show()
    }

    fun save(format: SaveFormat, onEnd: OnSaveEnd) {
        when (format) {
            SaveFormat.TXT -> {
                saveByText(onEnd)
            }

            SaveFormat.CSV -> {
                saveByCsv(onEnd)
            }
        }
    }

    fun saveByText(
        onEnd: OnSaveEnd,
    ) {
        saveAny("txt", onEnd) { list ->
            contentToString(list)
        }
    }

    /**
     * 保存为csv文件
     */
    fun saveByCsv(
        onEnd: OnSaveEnd,
    ) {
        saveAny("csv", onEnd) { list ->
            val builder = CsvHelper.build(
                "Package",
                "Time",
                "Activity",
                "SDK",
                "AdType",
                "Flag"
            )
            list.forEach { info ->
                val activityName = info.activityName
                builder.addLine(
                    info.packageName,
                    sdf.format(info.timeDate),
                    activityName,
                    info.sdkString,
                    info.adType,
                    info.flagString
                )
            }
            builder.build()
        }
    }

    /**
     * 耗时操作，需要在子线程执行
     */
    private fun contentToString(list: List<AppPageInfo>): String {
        val builder = StringBuilder()
        for (info in list) {
            builder.append(info.packageName).append(" ")
                .append(sdf.format(info.timeDate)).append(" ")
                .append(info.activityName).append(" ")
                .append(info.sdkString).append(" ")
                .append(info.adType).append(" ")
                .append(info.flagString).append("\n")
        }
        return builder.toString()
    }

    private fun saveAny(
        suffix: String,
        onEnd: OnSaveEnd,
        transition: (List<AppPageInfo>) -> String
    ) {
        appInfoDatabase.queryAll(appPackage) { list ->
            InfoSaveHelper.save(
                context = context,
                name = appName,
                suffix = suffix,
                infoProvider = {
                    transition(list.map { AppPageInfo.create(it) })
                },
                onEnd = {
                    onEnd.onEnd(it)
                }
            )
        }
    }

    enum class SaveFormat(val suffix: String) {
        TXT("txt"),
        CSV("csv"),
    }

    fun interface OnSaveEnd {
        fun onEnd(name: String)
    }

}