package com.lollipop.applist

import android.graphics.Outline
import android.graphics.drawable.AdaptiveIconDrawable
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
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lollipop.applist.databinding.ItemDialogLauncherBinding
import kotlin.math.max

class LauncherBottomSheetHelper(
    private val sheetView: View,
    private val dragHolderView: View,
    private val contentView: RecyclerView
) : BottomSheetBehavior.BottomSheetCallback() {

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

    val sheetPeekHeight = sheetView.resources.getDimension(
        R.dimen.launcher_peek_height
    ).toInt()
    private val bottomSheetBehavior = BottomSheetBehavior.from(sheetView)

    val isExpanded: Boolean
        get() {
            return bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
        }

    private val appList = mutableListOf<AppInfo>()

    private val adapter = AppAdapter(appList)

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
        bottomSheetBehavior.addBottomSheetCallback(this)
        dragHolderView.setOnClickListener {
            onDragHolderClick()
        }
        contentView.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
        contentView.adapter = adapter
        onSlide(0F)
    }

    fun getSheetPeekInsets(insets: Insets): Int {
        return sheetPeekInsets(insets, sheetPeekHeight)
    }

    fun expand() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun close() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun updateAppList(list: List<AppInfo>) {
        appList.clear()
        appList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    private fun onDragHolderClick() {
        if (isExpanded
            || bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
        ) {
            expand()
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        val expanded = newState == BottomSheetBehavior.STATE_EXPANDED
        backPressedCallback.isEnabled = expanded
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

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        onSlide(slideOffset)
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
            if (icon is AdaptiveIconDrawable) {
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

