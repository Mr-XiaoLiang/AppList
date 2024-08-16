package com.lollipop.applist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lollipop.applist.databinding.ItemDialogSdkKeywordsBinding

object SdkKeywordListDialog {

    fun show(activity: ComponentActivity) {
        val displayList = mutableListOf<SdkInfoDisplayInfo>()
        com.lollipop.applist.sdklist.SdkKeyword.forEach {
            displayList.add(SdkInfoDisplayInfo(it.label, it.keywordsString))
        }
        BottomSheetDialog(activity).apply {
            setContentView(R.layout.dialog_sdk_keywords)
            findViewById<RecyclerView>(R.id.recyclerView)?.let { recyclerView ->
                recyclerView.adapter = SdkKeywordInfoAdapter(displayList)
                recyclerView.layoutManager = LinearLayoutManager(
                    activity, RecyclerView.VERTICAL, false
                )
            }
            show()
        }
    }

    private class SdkKeywordInfoAdapter(
        private val sdkList: List<SdkInfoDisplayInfo>
    ) : RecyclerView.Adapter<SdkKeywordInfoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SdkKeywordInfoHolder {
            return SdkKeywordInfoHolder(
                ItemDialogSdkKeywordsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return sdkList.size
        }

        override fun onBindViewHolder(holder: SdkKeywordInfoHolder, position: Int) {
            holder.bind(sdkList[position])
        }

    }

    private class SdkKeywordInfoHolder(
        val binding: ItemDialogSdkKeywordsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(info: SdkInfoDisplayInfo) {
            binding.sdkLabelView.text = info.name
            binding.sdkKeywordsView.text = info.keywords
        }

    }

    private class SdkInfoDisplayInfo(
        val name: String,
        val keywords: String
    )

}