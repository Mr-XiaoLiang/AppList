package com.lollipop.applist

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lollipop.applist.databinding.ItemAppBinding


class AppAdapter(private val list: List<AppInfo>) :
    RecyclerView.Adapter<AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(
            ItemAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class AppViewHolder(
    private val viewBinding: ItemAppBinding
) : RecyclerView.ViewHolder(viewBinding.root) {

    init {
        viewBinding.root.setOnClickListener {
            onItemClick()
        }
    }

    private fun onItemClick() {
        val context = viewBinding.root.context
        val packageName = viewBinding.packageView.text?.toString() ?: return
        if (packageName.isEmpty()) {
            return
        }
        val labelName = viewBinding.labelView.text?.toString() ?: return
        if (labelName.isEmpty()) {
            return
        }
        AppOptionHelper.showOptionDialog(context, labelName, packageName)
    }

    fun bind(info: AppInfo) {
        viewBinding.labelView.text = info.name
        viewBinding.packageView.text = info.packageName
        viewBinding.appIconView.setImageDrawable(info.icon)
    }
}

private object AppOptionHelper {

    enum class OptionMenu(val label: String) {
        QUICK_ADD("收藏应用"),
        QUICK_REMOVE("取消收藏"),
        COPY("复制包名"),
        SETTING("应用设置"),
        OPEN("打开应用"),
        SDK("SDK")
    }

    fun showOptionDialog(context: Context, labelName: String, pkgName: String) {
        val menuList = OptionMenu.entries.filter {
            filterOption(context, pkgName, it)
        }
        val menuNameList = menuList.map { it.label }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle(labelName)
            .setItems(menuNameList) { dialog, which ->
                doOption(context, pkgName, menuList[which])
                dialog.dismiss()
            }
            .show()
    }

    private fun filterOption(context: Context, pkgName: String, optionMenu: OptionMenu): Boolean {
        return when (optionMenu) {
            OptionMenu.COPY -> true
            OptionMenu.SETTING -> true
            OptionMenu.OPEN -> true
            OptionMenu.QUICK_ADD -> !QuickAppHelper.isQuickApp(pkgName)
            OptionMenu.QUICK_REMOVE -> QuickAppHelper.isQuickApp(pkgName)
            OptionMenu.SDK -> true
        }
    }

    private fun doOption(context: Context, pkgName: String, optionMenu: OptionMenu) {
        when (optionMenu) {
            OptionMenu.COPY -> copyPackage(context, pkgName)
            OptionMenu.SETTING -> openPackageSetting(context, pkgName)
            OptionMenu.OPEN -> openApp(context, pkgName)
            OptionMenu.QUICK_ADD -> addQuickApp(context, pkgName)
            OptionMenu.QUICK_REMOVE -> removeQuickApp(context, pkgName)
            OptionMenu.SDK -> openSdkInfo(context, pkgName)
        }
    }

    private fun addQuickApp(context: Context, packageName: String) {
        QuickAppHelper.addQuickApp(packageName)
        QuickAppHelper.saveQuickApp(context)
    }

    private fun removeQuickApp(context: Context, packageName: String) {
        QuickAppHelper.removeQuickApp(packageName)
        QuickAppHelper.saveQuickApp(context)
    }

    private fun openApp(context: Context, packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "打开失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPackageSetting(context: Context, packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }

    private fun copyPackage(context: Context, packageName: String) {
        val clipboardManager = context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as? ClipboardManager ?: return
        clipboardManager.setPrimaryClip(
            ClipData(
                ClipDescription(
                    "",
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                ), ClipData.Item(packageName)
            )
        )
        Toast.makeText(context, "包名已复制", Toast.LENGTH_SHORT).show()
    }

    private fun openSdkInfo(context: Context, packageName: String) {
        AppSdkInfoActivity.start(context, packageName)
    }

}
