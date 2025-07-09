package com.lollipop.applist

import android.app.Application
import com.lollipop.applist.sdk.QuickAppHelper
import com.lollipop.applist.ui.state.AppLauncher
import com.lollipop.applist.ui.state.HookStateController

class AApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppLauncher.init(this)
        HookStateController.init(this)
        QuickAppHelper.init(this)
    }

}