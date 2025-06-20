package com.lollipop.applist.helper

import android.app.Activity
import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

object ChromeHelper {

    fun open(context: Context, url: String) {
        val tabsIntent = CustomTabsIntent.Builder().build();
        val uri = url.toUri()
        if (context is Activity) {
            tabsIntent.launchUrl(context, uri);
        } else {
            val intent = tabsIntent.intent;
            intent.setData(uri);
            context.startActivity(intent, tabsIntent.startAnimationBundle);
        }
    }

    private fun test() {
        // 向toolbar添加一个Action Button
        // ‘icon’是一张位图(Bitmap)，作为action button的图片资源使用

        // 'description'是一个字符串，作为按钮的无障碍描述所使用

        // 'pendingIntent' 是一个PendingIntent，当action button或者菜单项被点击时调用。
        // 在url作为data被添加之后，Chrome 会调用PendingIntent#send()方法。
        // 客户端应用会通过调用Intent#getDataString()获取到URL

        // 'tint'是一个布尔值，定义了Action Button是否应该被着色

    }

}