package com.lollipop.applist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.applist.databinding.ItemQuickAppBinding


class QuickAppAdapter(
    private val list: List<AppInfo>,
    private val onItemClick: (String) -> Unit
) :
    RecyclerView.Adapter<QuickAppHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickAppHolder {
        return QuickAppHolder(
            ItemQuickAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: QuickAppHolder, position: Int) {
        holder.bind(list[position])
    }

}

class QuickAppHolder(
    private val viewBinding: ItemQuickAppBinding,
    private val onItemClick: (String) -> Unit
) : RecyclerView.ViewHolder(viewBinding.root) {

    init {
        viewBinding.root.setOnClickListener {
            onItemClick()
        }
    }

    private var currentApp: AppInfo? = null

    fun bind(info: AppInfo) {
        viewBinding.appIconView.setImageDrawable(info.icon)
        currentApp = info
    }

    private fun onItemClick() {
        val packageName = currentApp?.packageName ?: return
        if (packageName.isEmpty()) {
            return
        }
        onItemClick(packageName)
    }
}