package com.lollipop.applist.sdklist

object OtherSDK {

    val list = arrayOf<SDK.Other>(
        sdk(
            label = "Android",
            keywords = listOf(
                "android.permission",
                "com.android.settings.permission",
                "com.android.vending",
                "androidx.work",
                "androidx.room",
                "androidx.startup",
                "androidx.core",
                "androidx.lifecycle",
            )
        ),
        sdk(label = "Unity", keywords = listOf("libil2cpp", "libunity")),
        sdk(label = "Flutter", keywords = listOf("libflutter")),
        sdk(label = "ReactNative", keywords = listOf("libreactnative")),
        sdk(label = "Xamarin", keywords = listOf("libxamarin")),
        sdk(label = "Cocos2d", keywords = listOf("libcocos2d", "libcocos2djs", "org.cocos2dx")),
        sdk(label = "Laya", keywords = listOf("liblaya")),
        sdk(label = "Egret", keywords = listOf("libegret")),
        sdk(label = "GooglePlay", keywords = listOf("com.google.android.play")),
    )

    fun sdk(label: String, website: String = "", keywords: List<String>): SDK.Other {
        return SDK.Other(
            label = label,
            website = website,
            keywords = keywords
        )
    }

}