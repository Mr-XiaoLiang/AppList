package com.lollipop.applist

data object SdkKeyword {

    val OTHER = Sdk("Other", listOf())

    private val sdkLists = listOf(
        Sdk("Tencent", listOf("tencent", "gdt")),
        Sdk("HuaWei", listOf("huawei", "hms")),
        Sdk("Honor", listOf("hihonor")),
        Sdk("Baidu", listOf("baidu")),
        Sdk("UMeng", listOf("umeng")),
        Sdk("Amazon", listOf("amazon")),
        Sdk("Fyber", listOf("fyber")),
        Sdk("Chartboost", listOf("chartboost")),
        Sdk("mBridge", listOf("mbridge", "msdk")),
        Sdk("Google", listOf("gms", "admob", "google")),
        Sdk("Yandex", listOf("yandex")),
        Sdk("ByteDance", listOf("bytedance")),
        Sdk("Vungle", listOf("vungle")),
        Sdk("Bigo", listOf("bigo")),
        Sdk("Tapjoy", listOf("tapjoy")),
        Sdk("AdHub", listOf("five_corp", "adhub")),
        Sdk("zMaticoo", listOf("maticoo")),
        Sdk("InMobi", listOf("inmobi")),
        Sdk("BitMachine", listOf("bidmachine")),
        Sdk("PubMatic", listOf("pubmatic")),
        Sdk("Square", listOf("squareup")),
        Sdk("Unity", listOf("unity3d")),
        Sdk("ironSource", listOf("ironsource")),
        Sdk("Facebook", listOf("facebook")),
        Sdk("PubNative", listOf("pubnative")),
        Sdk("AppLovin", listOf("applovin")),
        Sdk("iabTechlab", listOf("explorestack.iab")),
        Sdk("AdColony", listOf("adcolony")),
        Sdk("Smaato", listOf("smaato")),
        Sdk("Adjust", listOf("adjust")),
    )

    fun match(value: String): List<Sdk> {
        return sdkLists.filter { it.isMatch(value) }
    }

    class Sdk(
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