package com.lollipop.applist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.applist.databinding.ItemQuickAppBinding


class QuickAppAdapter(
    private val list: List<AppInfo>,
    private val onItemClick: (AppInfo) -> Unit
) :
    RecyclerView.Adapter<QuickAppHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickAppHolder {
        return QuickAppHolder(
            ItemQuickAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            ::onItemClick
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun onItemClick(position: Int) {
        if (position < 0 || position >= list.size) {
            return
        }
        onItemClick(list[position])
    }

    override fun onBindViewHolder(holder: QuickAppHolder, position: Int) {
        holder.bind(list[position])
    }

}

class QuickAppHolder(
    private val viewBinding: ItemQuickAppBinding,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(viewBinding.root) {

    init {
        viewBinding.root.setOnClickListener {
            onItemClick()
        }
    }

    fun bind(info: AppInfo) {
        viewBinding.appIconView.setImageDrawable(info.icon)
    }

    private fun onItemClick() {
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        onItemClick(adapterPosition)
    }
}