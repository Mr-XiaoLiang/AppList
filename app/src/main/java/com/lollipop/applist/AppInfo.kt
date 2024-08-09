package com.lollipop.applist

import android.graphics.drawable.Drawable

class AppInfo(
    val name: CharSequence,
    val packageName: String,
    val icon: Drawable,
    val launcherIcon: Drawable
) {

    val lowercaseName: String by lazy {
        name.toString().lowercase()
    }

    val lowercasePackage: String by lazy {
        packageName.lowercase()
    }

}