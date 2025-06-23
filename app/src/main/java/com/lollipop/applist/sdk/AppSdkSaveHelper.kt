package com.lollipop.applist.sdk

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lollipop.applist.data.InfoSaveHelper
import com.lollipop.applist.sdklist.AppSdkInfo

class AppSdkSaveHelper(
    private val context: Context,
    private val sdkInfo: AppSdkInfo
) {

    var appLabel: String = ""
    var packageName: String = ""

    fun save(onEnd: OnEndCallback) {
        val menuNameList = SaveFormat.entries.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle("$appLabel 保存为")
            .setItems(menuNameList) { dialog, which ->
                save(SaveFormat.entries[which], onEnd)
                dialog.dismiss()
            }
            .show()
    }

    fun save(format: SaveFormat, onEnd: OnEndCallback) {
        when (format) {
            SaveFormat.JSON -> {
                saveByJson(onEnd)
            }

            SaveFormat.CSV -> {
                saveByCsv(onEnd)
            }
        }
    }

    fun saveByJson(onEnd: OnEndCallback) {
        saveAny(SaveFormat.JSON.suffix, onEnd) {
            it.toJson().toString(4)
        }
    }

    fun saveByCsv(onEnd: OnEndCallback) {
        saveAny(SaveFormat.CSV.suffix, onEnd) {
            it.toCsv()
        }
    }

    private fun saveAny(
        suffix: String,
        onEnd: OnEndCallback,
        transition: (AppSdkInfo) -> String
    ) {
        InfoSaveHelper.save(
            context = context,
            suffix = suffix,
            name = getSaveFileName(appLabel, packageName),
            infoProvider = {
                transition(sdkInfo)
            },
            onEnd = {
                onEnd.onEnd(it)
            }
        )
    }

    private fun getSaveFileName(appLabel: String, packageName: String): String {
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

    enum class SaveFormat(val suffix: String) {
        JSON("json"),
        CSV("csv"),
    }

    fun interface OnEndCallback {
        fun onEnd(fileName: String)
    }

}