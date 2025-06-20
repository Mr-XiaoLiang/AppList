package com.lollipop.applist.sdklist

object ShareSDK {

    val list = arrayOf<SDK.Share>(
    )

    fun sdk(label: String, website: String = "", keywords: List<String>): SDK.Share {
        return SDK.Share(
            label = label,
            website = website,
            keywords = keywords
        )
    }

}