package com.lollipop.applist.sdklist

object PaySDK {

    val list = arrayOf<SDK.Pay>(
        sdk(label = "XiaoMi", keywords = listOf("com.xiaomi.billingclient"))
    )

    fun sdk(label: String, website: String = "", keywords: List<String>): SDK.Pay {
        return SDK.Pay(
            label = label,
            website = website,
            keywords = keywords
        )
    }

}