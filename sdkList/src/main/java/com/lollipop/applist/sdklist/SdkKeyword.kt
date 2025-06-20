package com.lollipop.applist.sdklist

object SdkKeyword {

    val OTHER = SDK.Other(
        label = "Other",
        website = "",
        keywords = listOf()
    )

    val sdkLists: List<SDK> by lazy {
        listOf(
            *AdsSDK.list,
            *TrackSDK.list,
            *ShareSDK.list,
            *PaySDK.list,
            *OtherSDK.list,
        )
    }

    fun forEach(callback: (SDK) -> Unit) {
        sdkLists.forEach(callback)
    }

    fun match(value: String): List<SDK> {
        return sdkLists.filter { it.isMatch(value) }
    }

}
