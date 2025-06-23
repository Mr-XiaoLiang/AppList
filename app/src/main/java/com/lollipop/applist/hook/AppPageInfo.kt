package com.lollipop.applist.hook

import com.lollipop.applist.data.AppInfoDatabase
import com.lollipop.applist.sdklist.AdType
import com.lollipop.applist.sdklist.SDK
import com.lollipop.applist.sdklist.SdkKeyword
import java.util.Date

class AppPageInfo(
    val activityInfo: AppInfoDatabase.AppActivityInfo,
    val sdkList: List<SDK>
) {

    companion object {
        fun create(info: AppInfoDatabase.AppActivityInfo): AppPageInfo {
            val sdkList = SdkKeyword.match(info.activityName)
            return AppPageInfo(info, sdkList)
        }
    }

    val packageName: String
        get() = activityInfo.packageName

    val activityName: String
        get() = activityInfo.activityName

    val timeDate: Date
        get() = activityInfo.timeDate

    val flag: Boolean
        get() = activityInfo.flag

    val flagString: String by lazy {
        flag.toString()
    }

    val adType: String by lazy {
        getAdType(sdkList, activityName)
    }

    val sdkString: String by lazy {
        sdkList.joinToString(separator = "、") { it.label }
    }

    val adDisplay: String by lazy {
        if (sdkList.isEmpty()) {
            ""
        } else {
            "$sdkString - $adType"
        }
    }

    private fun getAdType(sdk: List<SDK>, activity: String): String {
        val adTypeList = mutableListOf<AdType>()
        for (item in sdk) {
            if (item is SDK.ADS) {
                val adType = item.adKeyword.match(activity)
                if (adType != AdType.OTHER) {
                    adTypeList.add(adType)
                }
            }
        }
        if (adTypeList.isEmpty()) {
            return AdType.OTHER.name
        }
        return adTypeList.joinToString(separator = "、") { it.name }
    }

}