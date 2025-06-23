package com.lollipop.applist.sdklist

import org.json.JSONArray
import org.json.JSONObject

class AppSdkInfo {

    companion object {
        private val typeFilterMap = HashMap<Type, Boolean>()

        fun typeEnable(type: Type): Boolean {
            return typeFilterMap[type] ?: true
        }

        fun setTypeFilter(type: Type, enable: Boolean) {
            typeFilterMap[type] = enable
        }
    }

    private val platformMap = HashMap<String, Platform>()

    private val otherPlatform = Platform(SdkKeyword.OTHER)

    private var selfPlatform: Platform? = null

    val app = AppInfo()

    fun clear() {
        platformMap.clear()
        otherPlatform.clear()
        selfPlatform?.clear()
    }

    fun setSelfPackageName(packageName: String) {
        selfPlatform = if (packageName.isEmpty()) {
            null
        } else {
            Platform(SDK.Other("Self", "", listOf(packageName)))
        }
    }

    fun check(type: Type, value: String) {
        if (!typeEnable(type)) {
            return
        }
        var isMatchSelf = false
        selfPlatform?.let { self ->
            if (self.sdk.isMatch(value)) {
                self.add(type, value)
                isMatchSelf = true
            }
        }
        val ads = SdkKeyword.match(value)
        if (ads.isEmpty() && !isMatchSelf) {
            otherPlatform.add(type, value)
        } else {
            ads.forEach { ad ->
                val key = ad.label
                val platform = platformMap[key]
                if (platform != null) {
                    platform.add(type, value)
                } else {
                    val newPlatform = Platform(ad)
                    platformMap[key] = newPlatform
                    newPlatform.add(type, value)
                }
            }
        }
    }

    fun getList(): List<Platform> {
        val mutableList = platformMap.values.toMutableList()
        selfPlatform?.let {
            if (it.list.isNotEmpty()) {
                it.sort()
                mutableList.add(it)
            }
        }
        if (otherPlatform.list.isNotEmpty()) {
            otherPlatform.sort()
            mutableList.add(otherPlatform)
        }
        return mutableList
    }

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("AppInfo", getAppInfoJson())
        val list = getList()
        list.forEach { platform ->
            // SDK 对象
            val sdkLabel = if (platform.sdk is SDK.Other) {
                platform.sdk.label
            } else {
                "${platform.sdk.label}(${platform.sdk.typeName})"
            }
            val platformObj = jsonObject.optJSONObject(sdkLabel) ?: JSONObject().also {
                jsonObject.put(sdkLabel, it)
            }
            // 数据列表
            platform.list.forEach { item ->
                // 数据类型
                val itemType = item.type.label
                val itemArray = platformObj.optJSONArray(itemType) ?: JSONArray().also {
                    platformObj.put(itemType, it)
                }
                itemArray.put(item.value)
            }
        }
        return jsonObject
    }

    fun toCsv(): String {
        val builder = CsvHelper.build(
            "APP", "Package", "Platform", "PlatformType", "SdkType", "Value"
        )
        val list = getList()

        val appLabel = app.label
        val appPackageName = app.packageName

        list.forEach { platform ->
            val sdkLabel = platform.sdk.label
            val sdkTypeName = platform.sdk.typeName
            // 数据列表
            platform.list.forEach { item ->
                builder.addLine(
                    appLabel,
                    appPackageName,
                    sdkLabel,
                    sdkTypeName,
                    item.type.label,
                    item.value
                )
            }
        }
        return builder.build()
    }

    private fun getAppInfoJson(): JSONObject {
        val json = JSONObject()
        json.put("package", app.packageName)
        json.put("versionName", app.versionName)
        json.put("versionCode", app.versionCode)
        json.put("label", app.label)
        return json
    }

    class AppInfo {
        var packageName: String = ""
        var versionName: String = ""
        var versionCode: String = ""
        var label: String = ""
    }

    class Platform(
        val sdk: SDK
    ) {
        private val itemList = ArrayList<Item>()
        private val sourceList = ArrayList<String>()

        val list: List<Item>
            get() {
                return itemList
            }

        val source: List<String>
            get() {
                return sourceList
            }

        fun clear() {
            itemList.clear()
        }

        fun add(type: Type, value: String) {
            if (type == Type.SourceCode) {
                sourceList.add(value)
            } else {
                itemList.add(Item(type, value))
            }
        }

        fun sort() {
            itemList.sortBy { it.type.ordinal }
        }
    }

    enum class Type(val label: String, val color: Int) {
        Activity("Activity", 0xFFB50000.toInt()),
        Service("Service", 0xFFB57300.toInt()),
        Provider("Provider", 0xFF7FB500.toInt()),
        Receiver("Receiver", 0xFF00B57C.toInt()),
        MetaData("MetaData", 0xFF0076B5.toInt()),
        Permission("Permission", 0xFF9400B5.toInt()),
        Native("Native", 0xFF00DEB6.toInt()),
        SourceCode("SourceCode", 0xFF8DD338.toInt())
    }

    class Item(
        val type: Type,
        val value: String,
    ) {

        override fun toString(): String {
            return "Item(type=$type, value=$value)"
        }

    }

}