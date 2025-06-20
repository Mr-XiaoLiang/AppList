package com.lollipop.applist.sdklist

object TrackSDK {

    val list = arrayOf<SDK.Track>(
        sdk(label = "Umeng", keywords = listOf("com.umeng.analytics")),
        sdk(label = "Baidu", keywords = listOf("com.baidu.mobstat")),
        sdk(label = "Firebase", keywords = listOf("com.google.firebase"))
    )

    fun sdk(label: String, website: String = "", keywords: List<String>): SDK.Track {
        return SDK.Track(
            label = label,
            website = website,
            keywords = keywords
        )
    }

}