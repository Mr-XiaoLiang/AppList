package com.lollipop.applist.hook

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.WindowManager
import com.lollipop.applist.databinding.FloatHookBinding

class HookFloatingDelegate(
    private val context: Context,
    private val flagCallback: (pkg: String, cls: String) -> Boolean,
    private val notifyRefresh: () -> Unit
) {

    private val binding by lazy {
        FloatHookBinding.inflate(LayoutInflater.from(context))
    }

    private var currentPkg = ""
    private var currentClass = ""

    private var isInit = false

    private fun initView() {
        if (isInit) {
            return
        }
        binding.flagButton.isSelected = false
        isInit = true
        binding.flagButton.setOnClickListener {
            onFlagClick()
        }
        binding.refreshButton.setOnClickListener {
            onRefreshClick()
        }
    }

    private fun onFlagClick() {
        if (currentPkg.isEmpty() || currentClass.isEmpty()) {
            return
        }
        binding.flagButton.isSelected = flagCallback(currentPkg, currentClass)
    }

    private fun onRefreshClick() {
        notifyRefresh()
    }

    fun show() {
        initView()
        initFloating()
    }

    fun hide() {
        removeAlertView(binding.root)
    }

    @SuppressLint("SetTextI18n")
    fun update(pkg: String, cls: String) {
        this.currentPkg = pkg
        this.currentClass = cls
        this.binding.flagButton.isSelected = false
        binding.infoView.text = "${pkg}\n${cls}"
    }

    private fun initFloating() {
        val floatingView = binding.root
        addAlertView(floatingView) { m, v, p ->
            p.width = ViewGroup.LayoutParams.WRAP_CONTENT
            p.height = ViewGroup.LayoutParams.WRAP_CONTENT
            p.x = 0
            p.y = 0
            p.gravity = 0
            p.flags = buildFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            v.setOnTouchListener(FloatingDragListener { dx, dy ->
                val layoutParams = v.layoutParams
                if (layoutParams is WindowManager.LayoutParams) {
                    layoutParams.x += dx
                    layoutParams.y += dy
                    val screenSize = getScreenSize(m)
                    val maxX = (screenSize.width - v.width) / 2
                    val maxY = (screenSize.height - v.height) / 2
                    val minX = maxX * -1
                    val minY = maxY * -1
                    if (layoutParams.x < minX) {
                        layoutParams.x = minX
                    }
                    if (layoutParams.y < minY) {
                        layoutParams.y = minY
                    }
                    if (layoutParams.x > maxX) {
                        layoutParams.x = maxX
                    }
                    if (layoutParams.y > maxY) {
                        layoutParams.y = maxY
                    }
                    m.updateViewLayout(v, layoutParams)
                }
            })
        }
    }

    private fun getScreenSize(windowManager: WindowManager): Size {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            return Size(windowMetrics.bounds.width(), windowMetrics.bounds.height())
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    private fun buildFlags(vararg flags: Int): Int {
        var result = 0
        for (flag in flags) {
            result = result or flag
        }
        return result
    }

    private fun addAlertView(
        view: View,
        builder: (WindowManager, View, WindowManager.LayoutParams) -> Unit
    ): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        val windowManager = context.getSystemService(WindowManager::class.java)
        if (windowManager != null) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.format = PixelFormat.TRANSPARENT;
            builder(windowManager, view, layoutParams)
            windowManager.addView(view, layoutParams);
        }
        return layoutParams
    }

    private fun removeAlertView(view: View) {
        view.parent?.let { parent ->
            if (parent is ViewManager) {
                parent.removeView(view)
            }
        }
//        context.getSystemService(WindowManager::class.java)?.removeView(view)
    }

}