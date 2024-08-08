package com.lollipop.applist

data object SdkKeyword {

    val OTHER = Sdk(
        "Other",
        listOf()
    )

    private val sdkLists = listOf(
        Sdk(
            "Tencent",
            listOf("tencent", "gdt", "com.qq.", "yaq.gdtadv")
        ),
        Sdk(
            "HuaWei",
            listOf("huawei", "hms")
        ),
        Sdk(
            "Honor",
            listOf("hihonor")
        ),
        Sdk(
            "OPPO",
            listOf("oppo", "heytap", "oneplus")
        ),
        Sdk(
            "vivo",
            listOf("vivo")
        ),
        Sdk(
            "XiaoMi",
            listOf("xiaomi", "miui", "hyper", "com.mi.")
        ),
        Sdk(
            "Microsoft",
            listOf("microsoft")
        ),
        Sdk(
            "Chrome",
            listOf("chrome", "chromium")
        ),
        Sdk(
            "Baidu",
            listOf("baidu")
        ),
        Sdk(
            "Bugly",
            listOf("bugly", "libcrashlytics")
        ),
        Sdk(
            "UMeng",
            listOf("umeng")
        ),
        Sdk(
            "Amazon",
            listOf("amazon")
        ),
        Sdk(
            "Fyber",
            listOf("fyber")
        ),
        Sdk(
            "TradPlus",
            listOf("com.tp.", "com.tradplus.")
        ),
        Sdk(
            "Sigmob",
            listOf("sigmob")
        ),
        Sdk(
            "快手",
            listOf("com.kwad.", "com.ksad", "com.kuaishou", "com.kuai", "com.yxcorp", "kssdk-ad")
        ),
        Sdk(
            "TapTap",
            listOf("com.tapsdk.")
        ),
        Sdk(
            "Chartboost",
            listOf("chartboost")
        ),
        Sdk(
            "mBridge",
            listOf("mbridge", ".msdk.")
        ),
        Sdk(
            "Google",
            listOf(".gms.", ".admob.", "google")
        ),
        Sdk(
            "Yandex",
            listOf("yandex")
        ),
        Sdk(
            "ByteDance",
            listOf("bytedance", "com.byted", "com.bykv.vk", "com.ttshell", "com.ss")
        ),
        Sdk(
            "Alibaba",
            listOf("taobao", "alibaba", "alipay")
        ),
        Sdk(
            "HaiLiang",
            listOf("com.hailiang.")
        ),
        Sdk(
            "Vungle",
            listOf("vungle")
        ),
        Sdk(
            "Bigo",
            listOf("bigo")
        ),
        Sdk(
            "Tapjoy",
            listOf("tapjoy")
        ),
        Sdk(
            "AdHub",
            listOf("five_corp", "adhub")
        ),
        Sdk(
            "zMaticoo",
            listOf("maticoo")
        ),
        Sdk(
            "InMobi",
            listOf("inmobi")
        ),
        Sdk(
            "BitMachine",
            listOf("bidmachine")
        ),
        Sdk(
            "PubMatic",
            listOf("pubmatic")
        ),
        Sdk(
            "Square",
            listOf("squareup")
        ),
        Sdk(
            "Unity",
            listOf("unity3d", "libunity", "libil2cpp")
        ),
        Sdk(
            "UnityAd",
            listOf("com.unity3d.services.ads")
        ),
        Sdk(
            "ironSource",
            listOf("ironsource")
        ),
        Sdk(
            "Facebook",
            listOf("facebook")
        ),
        Sdk(
            "PubNative",
            listOf("pubnative")
        ),
        Sdk(
            "AppLovin",
            listOf("applovin")
        ),
        Sdk(
            "iabTechlab",
            listOf("explorestack.iab")
        ),
        Sdk(
            "AdColony",
            listOf("adcolony")
        ),
        Sdk(
            "Smaato",
            listOf("smaato")
        ),
        Sdk(
            "Adjust",
            listOf("adjust")
        ),
        Sdk(
            "10086",
            listOf("com.cmic.sso.sdk")
        ),
        Sdk(
            "360",
            listOf("com.qihoo.")
        ),
        Sdk(
            "10010",
            listOf("com.unicom.xiaowo.")
        ),
        Sdk(
            "10000",
            listOf("cn.com.chinatelecom")
        ),
        Sdk(
            "MeiZu",
            listOf("meizu")
        ),
        Sdk(
            "Sina",
            listOf("sina", "weibo")
        ),
        Sdk(
            "CPC",
            listOf("com.iclicash.advlib")
        ),
        Sdk(
            "光粒星辉",
            listOf("com.qttsdk.glxh", "com.aggmoread.sdk")
        ),
        Sdk(
            "数盟",
            listOf("cn.shuzilm")
        ),
        Sdk(
            "JD",
            listOf("com.jd")
        ),
        Sdk(
            "趣头条",
            listOf("com.inno", "com.jifen", "com.innotech")
        )
    )

    fun forEach(callback: (Sdk) -> Unit) {
        sdkLists.forEach(callback)
    }

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
