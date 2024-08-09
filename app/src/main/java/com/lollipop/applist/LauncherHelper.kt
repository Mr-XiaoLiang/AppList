package com.lollipop.applist

import android.annotation.SuppressLint
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.sidesheet.SideSheetBehavior
import com.google.android.material.sidesheet.SideSheetCallback
import com.lollipop.applist.databinding.ItemDialogLauncherBinding
import kotlin.math.max

sealed class LauncherSheetHelper(
    protected val contentView: RecyclerView
) {

    companion object {
        fun sheetPeekInsets(insets: Insets, peekHeight: Int): Int {
            return max(insets.top, peekHeight + insets.bottom)
        }
    }

    val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            close()
        }
    }

    private val contentHelper = LauncherContentHelper(contentView)

    abstract fun getSheetPeekInsets(insets: Insets): Rect

    abstract fun expand()

    abstract fun close()

    fun updateAppList(list: List<AppInfo>) {
        contentHelper.updateAppList(list)
    }

    class BottomSheet(
        private val sheetView: View,
        private val dragHolderView: View,
        contentView: RecyclerView
    ) : LauncherSheetHelper(
        contentView
    ) {

        val sheetPeekHeight = sheetView.resources.getDimension(
            R.dimen.launcher_peek_height
        ).toInt()
        private val bottomSheetBehavior = BottomSheetBehavior.from(sheetView)

        val isExpanded: Boolean
            get() {
                return bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
            }

        private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChanged(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlide(slideOffset)
            }

        }

        init {
            ViewCompat.setOnApplyWindowInsetsListener(sheetView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                bottomSheetBehavior.peekHeight = sheetPeekInsets(systemBars, sheetPeekHeight)
                dragHolderView.updateLayoutParams<ViewGroup.LayoutParams> {
                    height = max(systemBars.top, dragHolderView.minimumHeight)
                }
                insets
            }
            bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
            dragHolderView.setOnClickListener {
                onDragHolderClick()
            }
            onSlide(0F)
            close()
            onStateChanged(BottomSheetBehavior.STATE_COLLAPSED)
            sheetView.setOnClickListener {
                // 空的click事件避免点击穿透
            }
        }

        override fun getSheetPeekInsets(insets: Insets): Rect {
            return Rect(
                insets.left,
                insets.top,
                insets.right,
                sheetPeekInsets(insets, sheetPeekHeight)
            )
        }

        private fun onDragHolderClick() {
            if (isExpanded
                || bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
            ) {
                expand()
            }
        }

        private fun onStateChanged(newState: Int) {
            val expanded = newState == BottomSheetBehavior.STATE_EXPANDED
            val collapsed = newState == BottomSheetBehavior.STATE_COLLAPSED
            backPressedCallback.isEnabled = expanded
            contentView.isInvisible = collapsed
            dragHolderView.animate().apply {
                cancel()
                alpha(
                    if (expanded) {
                        0F
                    } else {
                        1F
                    }
                )
                start()
            }
        }

        private fun onSlide(slideOffset: Float) {
            var fl = slideOffset * 10
            if (fl < 0) {
                fl = 0F
            }
            if (fl > 1) {
                fl = 1F
            }
            Log.d("Launcher", "onSlide: $slideOffset  ==>  $fl")
            contentView.alpha = fl
        }

        override fun expand() {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        override fun close() {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }

    class SideSheet(
        private val sheetView: View,
        private val dragHolderView: View,
        contentView: RecyclerView
    ) : LauncherSheetHelper(
        contentView
    ) {

        private val sideSheetBehavior = SideSheetBehavior.from(sheetView)

        private val sideSheetCallback = object : SideSheetCallback() {
            override fun onStateChanged(sheet: View, newState: Int) {
                onStateChanged(newState)
            }

            override fun onSlide(sheet: View, slideOffset: Float) {
            }
        }

        init {
            dragHolderView.isVisible = false
            ViewCompat.setOnApplyWindowInsetsListener(sheetView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            sideSheetBehavior.addCallback(sideSheetCallback)
            onStateChanged(BottomSheetBehavior.STATE_COLLAPSED)
            sheetView.setOnClickListener {
                // 空的click事件避免点击穿透
            }
        }

        private fun onStateChanged(newState: Int) {
            val expanded = newState == BottomSheetBehavior.STATE_EXPANDED
            backPressedCallback.isEnabled = expanded
        }

        override fun getSheetPeekInsets(insets: Insets): Rect {
            return Rect(insets.left, insets.top, insets.right, insets.bottom)
        }

        override fun expand() {
            sideSheetBehavior.expand()
        }

        override fun close() {
            sideSheetBehavior.hide()
        }

    }

}

class LauncherContentHelper(
    val contentView: RecyclerView
) {

    private val appList = mutableListOf<AppInfo>()

    private val adapter = AppAdapter(appList)

    init {
        contentView.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
        contentView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAppList(list: List<AppInfo>) {
        appList.clear()
        appList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    private class AppAdapter(private val list: List<AppInfo>) :
        RecyclerView.Adapter<AppViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            return AppViewHolder(
                ItemDialogLauncherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                ::onItemClick
            )
        }

        private fun onItemClick(itemView: View, position: Int) {
            if (position < 0 || position >= list.size) {
                return
            }
            val info = list[position]
            AppOptionHelper.showOptionDialog(
                itemView.context,
                info.name.toString(),
                info.packageName
            )
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    private class AppViewHolder(
        private val viewBinding: ItemDialogLauncherBinding,
        private val onItemClick: (View, Int) -> Unit
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        init {
            viewBinding.root.setOnClickListener {
                onItemClick()
            }
            viewBinding.cardContentView.clipToOutline = true
            val clipRoundRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8F,
                viewBinding.root.resources.displayMetrics
            )
            viewBinding.cardContentView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, clipRoundRadius)
                }
            };
        }

        private fun onItemClick() {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(itemView, adapterPosition)
            }
        }

        fun bind(info: AppInfo) {
            viewBinding.labelView.text = info.name
            val icon = info.launcherIcon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && icon is AdaptiveIconDrawable) {
                viewBinding.appIconView.setImageDrawable(icon.foreground)
                viewBinding.appIconView.scaleX = 1.6F
                viewBinding.appIconView.scaleY = 1.6F
                viewBinding.cardContentView.background = icon.background
            } else {
                viewBinding.appIconView.scaleX = 1F
                viewBinding.appIconView.scaleY = 1F
                viewBinding.appIconView.setImageDrawable(icon)
                viewBinding.cardContentView.setBackgroundResource(R.color.launcherItemBackground)
            }
        }
    }
}

