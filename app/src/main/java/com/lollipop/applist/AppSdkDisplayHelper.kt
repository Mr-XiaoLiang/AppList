package com.lollipop.applist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lollipop.applist.databinding.ItemAppAdkInfoFooterBinding
import com.lollipop.applist.databinding.ItemAppAdkInfoPartBinding
import com.lollipop.applist.databinding.ItemAppAdkInfoTitleBinding

object AppSdkDisplayHelper {

    fun create(): Delegate {
        return Delegate()
    }

    class Delegate {

        val dataList = mutableListOf<SdkInfo>()

        val adapter = Adapter(dataList)

        @SuppressLint("NotifyDataSetChanged")
        fun update(context: Context, list: List<AppSdkInfo.Platform>) {
            dataList.clear()
            dataList.addAll(transform(context, list))
            adapter.notifyDataSetChanged()
        }

        fun attach(recyclerView: RecyclerView) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                recyclerView.context, RecyclerView.VERTICAL, false
            )
        }

    }

    fun transform(context: Context, list: List<AppSdkInfo.Platform>): List<SdkInfo> {
        val colorA = ContextCompat.getColor(context, R.color.itemBackgroundA)
        val colorB = ContextCompat.getColor(context, R.color.itemBackgroundB)
        var colorMode = true
        val resultList = mutableListOf<SdkInfo>()
        for (platform in list) {
            val color = if (colorMode) {
                colorA
            } else {
                colorB
            }
            resultList.add(SdkInfo.Title(platform.sdk, color))
            for (item in platform.list) {
                resultList.add(SdkInfo.Part(item, color))
            }
            resultList.add(SdkInfo.Footer(color))
            colorMode = !colorMode
        }
        return resultList
    }

    sealed class SdkInfo {

        class Title(val sdk: SdkKeyword.Sdk, val background: Int) : SdkInfo()

        class Part(val item: AppSdkInfo.Item, val background: Int) : SdkInfo()

        class Footer(val background: Int) : SdkInfo()

    }

    class Adapter(private val list: List<SdkInfo>) : RecyclerView.Adapter<SdkInfoHolder>() {

        companion object {
            const val TYPE_TITLE = 1
            const val TYPE_ITEM = 2
            const val TYPE_FOOTER = 3
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SdkInfoHolder {
            return when (viewType) {
                TYPE_TITLE -> {
                    SdkInfoHolder.GroupTitle(
                        ItemAppAdkInfoTitleBinding.inflate(
                            LayoutInflater.from(
                                parent.context
                            ),
                            parent,
                            false
                        )
                    )
                }

                TYPE_ITEM -> {
                    SdkInfoHolder.GroupItem(
                        ItemAppAdkInfoPartBinding.inflate(
                            LayoutInflater.from(
                                parent.context
                            ),
                            parent,
                            false
                        )
                    )
                }

                TYPE_FOOTER -> {
                    SdkInfoHolder.GroupFooter(
                        ItemAppAdkInfoFooterBinding.inflate(
                            LayoutInflater.from(
                                parent.context
                            ),
                            parent,
                            false
                        )
                    )
                }

                else -> {
                    throw IllegalArgumentException("Unknown view type: $viewType")
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: SdkInfoHolder, position: Int) {
            val info = list[position]
            when (holder) {
                is SdkInfoHolder.GroupTitle -> {
                    if (info is SdkInfo.Title) {
                        holder.bind(info)
                    }
                }

                is SdkInfoHolder.GroupItem -> {
                    if (info is SdkInfo.Part) {
                        holder.bind(info)
                    }
                }

                is SdkInfoHolder.GroupFooter -> {
                    if (info is SdkInfo.Footer) {
                        holder.bind(info)
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (list[position]) {
                is SdkInfo.Title -> {
                    TYPE_TITLE
                }

                is SdkInfo.Part -> {
                    TYPE_ITEM
                }

                is SdkInfo.Footer -> {
                    TYPE_FOOTER
                }
            }
        }

    }

    sealed class SdkInfoHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        class GroupTitle(private val binding: ItemAppAdkInfoTitleBinding) : SdkInfoHolder(binding) {

            fun bind(info: SdkInfo.Title) {
                binding.adLabelView.text = info.sdk.label
                binding.root.setBackgroundColor(info.background)
            }

        }

        class GroupItem(private val binding: ItemAppAdkInfoPartBinding) : SdkInfoHolder(binding) {

            private val typeBackgroundDrawable = TypeBackgroundDrawable()

            init {
                binding.typeView.background = typeBackgroundDrawable
                typeBackgroundDrawable.radius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2F,
                    itemView.resources.displayMetrics
                )
                typeBackgroundDrawable.strokeWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    1F,
                    itemView.resources.displayMetrics
                )
            }

            fun bind(info: SdkInfo.Part) {
                binding.root.setBackgroundColor(info.background)
                binding.typeView.text = info.item.type.label
                binding.typeView.setTextColor(info.item.type.color)
                typeBackgroundDrawable.color = info.item.type.color
                binding.partValueView.text = info.item.value
            }

        }

        class GroupFooter(private val binding: ItemAppAdkInfoFooterBinding) :
            SdkInfoHolder(binding) {

            fun bind(info: SdkInfo.Footer) {
                binding.root.setBackgroundColor(info.background)
            }

        }

    }

    private class TypeBackgroundDrawable : Drawable() {

        private val paint = Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            isDither = true
        }

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        var strokeWidth: Float
            get() {
                return paint.strokeWidth
            }
            set(value) {
                paint.strokeWidth = value
            }

        var radius: Float = 5F

        private val boundsF = RectF()

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            val halfStrokeWidth = strokeWidth * 0.5F
            boundsF.set(
                bounds.left + halfStrokeWidth,
                bounds.top + halfStrokeWidth,
                bounds.right - halfStrokeWidth,
                bounds.bottom - halfStrokeWidth
            )
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRoundRect(boundsF, radius, radius, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

}