package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
import jadx.api.JadxDecompiler

interface JadxInfoDelegate {

    val sdkInfo: AppSdkInfo

    val manifest: ManifestParse

    fun parseSdkInfo()

    fun reset(jadx: JadxDecompiler, progressListener: JadxDecompiler.ProgressListener)

}