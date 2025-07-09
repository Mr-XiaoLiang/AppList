package com.lollipop.applist.desktop.state

object AppPlatform {

    val isMacOS: Boolean by lazy {
        osName.contains("mac", ignoreCase = true)
    }

    val osName: String by lazy {
        System.getProperty("os.name")
    }

}