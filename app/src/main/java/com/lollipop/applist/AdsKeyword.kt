package com.lollipop.applist

data object AdsKeyword {

    val OTHER = Ads("其他", listOf())

    private val adsList = listOf(
        Ads("腾讯", listOf("tencent", "gdt")),
        Ads("华为", listOf("huawei", "hms")),
        Ads("百度", listOf("baidu")),
        Ads("友盟", listOf("umeng")),
        Ads("Amazon", listOf("amazon")),
        Ads("Fyber", listOf("fyber")),
        Ads("Chartboost", listOf("chartboost")),
        Ads("mBridge", listOf("mbridge", "msdk")),
        Ads("Google", listOf("gms", "admob", "google")),
        Ads("Yandex", listOf("yandex")),
        Ads("ByteDance", listOf("bytedance", "gromore")),
        Ads("Vungle", listOf("vungle")),
        Ads("Bigo", listOf("bigo")),
        Ads("Tapjoy", listOf("tapjoy")),
        Ads("AdHub", listOf("five_corp", "adhub")),
        Ads("zMaticoo", listOf("maticoo")),
        Ads("InMobi", listOf("inmobi")),
        Ads("BitMachine", listOf("bidmachine")),
        Ads("PubMatic", listOf("pubmatic")),
        Ads("Square", listOf("squareup")),
        Ads("Unity", listOf("unity3d")),
        Ads("ironSource", listOf("ironsource")),
        Ads("Facebook", listOf("facebook")),
        Ads("PubNative", listOf("pubnative")),
        Ads("AppLovin", listOf("applovin")),
        Ads("iabTechlab", listOf("iab")),
        Ads("AdColony", listOf("adcolony")),
        Ads("Smaato", listOf("smaato")),
        Ads("Adjust", listOf("adjust"))
    )

    fun match(value: String): List<Ads> {
        return adsList.filter { it.isMatch(value) }
    }

    class Ads(
        val label: String,
        val keywords: List<String>
    ) {

        fun isMatch(text: String): Boolean {
            return keywords.any {
                text.contains(it, ignoreCase = true)
            }
        }

    }

}