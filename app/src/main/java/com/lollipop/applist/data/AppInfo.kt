package com.lollipop.applist.data

import android.graphics.drawable.Drawable
import com.google.accompanist.drawablepainter.DrawablePainter

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

    val nameString by lazy {
        name.toString()
    }

    val key: String by lazy {
        packageName
    }

    val painter: DrawablePainter by lazy {
        DrawablePainter(icon)
    }

}