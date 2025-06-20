package com.lollipop.applist.sdklist

object SdkKeyword {

    val OTHER = SDK.Other(
        label = "Other",
        website = "",
        keywords = listOf()
    )

    val sdkLists = listOf<SDK>(
        ads(label = "A4G", website = "https://a4g.com/", keywords = listOf()),
        ads(
            label = "Adcolony",
            website = "https://www.constructcollection.com/documentations/admob/adcolony-ads",
            keywords = listOf("com.adcolony")
        ),
        ads(
            label = "AdHub",
            website = "http://www.hubcloud.com.cn/view/about.html",
            keywords = listOf("com.hubcloud")
        ),
        ads(
            label = "Google",
            website = "https://admanager.google.com/intl/zh-CN_cn/home/",
            keywords = listOf("com.google.android.gms.ads")
        ),
        ads(label = "AlgoriX", website = "https://www.algorix.co/", keywords = listOf("com.alxad")),
        ads(
            label = "Amazon",
            website = "https://aps.amazon.com/aps/index.html",
            keywords = listOf("com.amazon.aps.ads", "com.amazon.device.ads")
        ),
        ads(label = "Applovin", website = "https://www.applovin.com/cn/", keywords = listOf("com.applovin.sdk")),
        ads(label = "AppNext", website = "https://www.appnext.com/", keywords = listOf("com.appnext.ads")),
        ads(label = "Baidu", website = "https://union.baidu.com/bqt/#/", keywords = listOf("com.baidu.mobads")),
        ads(label = "BeesAds", website = "https://www.beesads.com/", keywords = listOf("com.beesads.sdk")),
        ads(label = "Beizi", website = "http://www.beizi.biz/#/home/index", keywords = listOf("com.beizi")),
        ads(label = "BidMachine", website = "https://bidmachine.io/", keywords = listOf("io.bidmachine")),
        ads(label = "Bigo", website = "https://www.bigoads.com/", keywords = listOf("sg.bigo.ads")),
        ads(
            label = "Chartboost",
            website = "https://docs.chartboost.com/zh/monetization/get-started/",
            keywords = listOf("com.chartboost.sdk")
        ),
        ads(label = "Criteo", website = "https://www.criteo.com/", keywords = listOf("com.criteo.publisher")),
        ads(
            label = "CSJ(ByteDance)",
            website = "https://www.csjplatform.com/",
            keywords = listOf("com.bytedance.sdk.openadsdk")
        ),
        ads(
            label = "DigitalTurbine(Fyber)",
            website = "https://www.digitalturbine.com/",
            keywords = listOf("com.digitalturbine", "com.fyber")
        ),
        ads(
            label = "Helium(Chartboost)",
            website = "https://www.helium10.com/tools/advertising/helium10ads/",
            keywords = listOf("com.chartboost.heliumsdk", "com.chartboost.chartboostmediationsdk")
        ),
        ads(
            label = "Honor",
            website = "https://developer.honor.com/alliance/index.html#/",
            keywords = listOf("com.hihonor.adsdk")
        ),

        ads(
            label = "HuaWei",
            website = "https://developer.huawei.com/consumer/cn/huawei-ads",
            keywords = listOf("com.huawei.hms.ads", "com.huawei.openalliance.ad")
        ),
        ads(label = "HyprMX", website = "https://www.hyprmx.com/", keywords = listOf("com.hyprmx.android")),
        ads(
            label = "IMA(Google)",
            website = "https://developers.google.com/interactive-media-ads?hl=zh-cn",
            keywords = listOf("com.google.ads.interactivemedia")
        ),
        ads(
            label = "InMobi",
            website = "https://www.inmobi.cn/",
            keywords = listOf("com.inmobi.ads", "com.inmobi.sdk")
        ),
        ads(
            label = "IronSource",
            website = "https://developers.is.com/",
            keywords = listOf("com.unity3d.ironsourceads", "com.ironsource.sdk", "com.ironsource.mediationsdk")
        ),
        ads(
            label = "Kidoz",
            website = "https://www.kidoz.net/",
            keywords = listOf("net.kidoz.ads", "com.kpadplayer.sdk.ads")
        ),
        ads(label = "KuaiShou", website = "https://ad.e.kuaishou.com/welcome", keywords = listOf("com.kwad.sdk.api")),
        ads(label = "Kwai", website = "https://ads.kwai.com/", keywords = listOf("com.kwai.network")),
        ads(label = "Liftoff(Vungle)", website = "https://liftoff.io/", keywords = listOf("com.vungle.ads")),
        ads(
            label = "Line",
            website = "https://tw.linebiz.com/service/display-solutions/line-ads-platform/",
            keywords = listOf("com.five_corp.ad")
        ),
        ads(
            label = "Maio",
            website = "https://maio.jp/",
            keywords = listOf("jp.maio.sdk")
        ),
        ads(label = "MangoX", website = "https://mangox.io/", keywords = listOf()),
        ads(label = "Meta", website = "https://www.facebook.com/business/ads/", keywords = listOf("com.facebook.ads")),
        ads(label = "Microsoft", website = "https://ads.microsoft.com/", keywords = listOf()),
        ads(label = "Mintegral", website = "https://www.mintegral.com/en", keywords = listOf("com.mbridge.msdk")),
        ads(
            label = "MobileFuse",
            website = "https://docs.mobilefuse.com/docs/android-banner-ads",
            keywords = listOf("com.mobilefuse.sdk")
        ),
        ads(label = "Moloco", website = "https://help.moloco.com/hc/zh-cn", keywords = listOf("com.moloco.sdk")),
        ads(label = "OPPO", website = "https://u.oppomobile.com/#/", keywords = listOf("com.opos.mobad")),
        ads(label = "Ogury", website = "https://ogury.com/", keywords = listOf("com.ogury.ad")),
        ads(
            label = "Pangle(ByteDance)",
            website = "https://www.pangleglobal.com/zh",
            keywords = listOf("com.bytedance.sdk.openadsdk")
        ),
        ads(
            label = "PremiumAds",
            website = "https://premiumads.net/",
            keywords = listOf()
        ),
        ads(label = "PubMatic", website = "https://pubmatic.com/", keywords = listOf("com.pubmatic.sdk")),
        ads(label = "ReklamUp", website = "https://reklamup.com/", keywords = listOf()),
        ads(
            label = "Sigmob",
            website = "https://www.sigmob.com/home",
            keywords = listOf("com.sigmob.sdk", "com.sigmob.windad")
        ),
        ads(label = "Smaato", website = "https://www.smaato.com/", keywords = listOf("com.smaato.sdk")),
        ads(
            label = "Start.io",
            website = "https://www.start.io/",
            keywords = listOf("com.startapp.sdk", "com.startapp.motiondetector", "com.startapp.simple")
        ),
        ads(
            label = "SuperAwesome",
            website = "https://www.superawesome.com/",
            keywords = listOf("tv.superawesome.sdk")
        ),
        ads(
            label = "TanX(AliMM)",
            website = "https://mu.tanx.com/about.htm",
            keywords = listOf("com.alimm.tanx")
        ),
        ads(label = "TapTap", website = "https://biz.taptap.cn/", keywords = listOf("com.tapsdk.tapad")),
        ads(
            label = "Tapjoy",
            website = "https://dev.tapjoy.com/cn/support/Ad-Behavioural-Targeting",
            keywords = listOf("com.tapjoy")
        ),
        ads(label = "TaurusX", website = "https://taurusx.com/", keywords = listOf("com.taurusx.tax")),
        ads(label = "Tencent", website = "https://ad.qq.com/", keywords = listOf("com.qq.e.ads", "com.qq.e.mediation")),
        ads(
            label = "TradPlus",
            website = "https://www.tradplusad.com/",
            keywords = listOf("com.tradplus.ads", "com.tradplus.meditaiton")
        ),
        ads(
            label = "TopOn",
            website = "https://help.toponad.net/cn",
            keywords = listOf("com.anythink")
        ),
        ads(label = "UnityAds", website = "https://unity.com/products/unity-ads", keywords = listOf("com.unity3d.ads")),
        ads(label = "Verve(PubNative)", website = "https://verve.com/", keywords = listOf("net.pubnative.lite.sdk")),
        ads(label = "VK(MyTarget)", website = "https://ads.vk.com/en", keywords = listOf("com.my.target")),
//        ads(label = "XiaoMi", website = "http://ad.mi.com/", keywords = listOf()),
        ads(
            label = "MiMo",
            website = "https://dev.mi.com/xiaomihyperos/advertisement",
            keywords = listOf("com.miui.zeus.mimo")
        ),
        ads(
            label = "Columbus",
            website = "https://global.e.mi.com/doc/zh/mi_ads_guide.html",
            keywords = listOf("com.zeus.gmc.sdk.mobileads.columbus")
        ),
        ads(label = "YSONetwork", website = "https://www.ysonetwork.com/", keywords = listOf("com.ysocorp.ysonetwork")),
        ads(
            label = "Yandex",
            website = "https://ads.yandex.com/",
            keywords = listOf("com.yandex.mobile.ads", "com.monetization.ads")
        ),
        ads(label = "vivo", website = "https://adnet.vivo.com.cn/home", keywords = listOf("com.vivo.mobilead")),
        ads(label = "zMaticoo", website = "https://www.zmaticoo.com/", keywords = listOf("com.maticoo.sdk")),
    )

    private fun ads(label: String, website: String, keywords: List<String>): SDK.ADS {
        return SDK.ADS(
            label = label,
            website = website,
            keywords = keywords
        )
    }

    fun forEach(callback: (SDK) -> Unit) {
        sdkLists.forEach(callback)
    }

    fun match(value: String): List<SDK> {
        return sdkLists.filter { it.isMatch(value) }
    }

}
