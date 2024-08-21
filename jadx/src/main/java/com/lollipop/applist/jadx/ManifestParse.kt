package com.lollipop.applist.jadx

import com.lollipop.applist.sdklist.AppSdkInfo
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


sealed class ManifestParse {

    companion object {
        const val ATTRIBUTE_ANDROID_NAME = "android:name"
        const val ATTRIBUTE_ANDROID_VALUE = "android:value"
        const val ATTRIBUTE_ANDROID_REQUIRED = "android:required"
    }

    abstract val manifestSrc: String

    fun reload() {
        pkgNameImpl = null
        onReload()
    }

    protected abstract fun onReload()

    class FromString(val valueProvider: () -> String) : ManifestParse() {
        override var manifestSrc: String = ""

        override fun onReload() {
            manifestSrc = valueProvider()
        }

    }

    class FromFile(private val file: File) : ManifestParse() {
        override var manifestSrc = ""

        override fun onReload() {
            manifestSrc = file.readText()
        }
    }

    private var pkgNameImpl: String? = null

    val pkgName: String
        get() {
            val impl = pkgNameImpl
            if (impl == null) {
                val newImpl = parsePackageName()
                pkgNameImpl = newImpl
                return newImpl
            }
            return impl
        }

    private fun parsePackageName(): String {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val dom = db.parse(ByteArrayInputStream(manifestSrc.toByteArray()))
        val nodeList = dom.getElementsByTagName("manifest")
        var pkgResult = ""
        nodeList.forEach { manifest ->
            try {
                val pkg = manifest.attributes.getNamedItem("package")?.nodeValue ?: ""
                if (pkg.isNotEmpty()) {
                    pkgResult = pkg
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return pkgResult
    }

    fun parse(out: AppSdkInfo) {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val dom = db.parse(ByteArrayInputStream(manifestSrc.toByteArray()))
        val nodeList = dom.getElementsByTagName("manifest")
        nodeList.forEach { manifest ->
            out.app.versionCode = try {
                manifest.attributes.getNamedItem("android:versionCode")?.nodeValue ?: ""
            } catch (e: Exception) {
                ""
            }
            out.app.versionName = try {
                manifest.attributes.getNamedItem("android:versionName")?.nodeValue ?: ""
            } catch (e: Exception) {
                ""
            }
            out.app.packageName = try {
                manifest.attributes.getNamedItem("package")?.nodeValue ?: ""
            } catch (e: Exception) {
                ""
            }
            val childNodes = manifest.childNodes
            childNodes.forEach { pkgNode ->
                when (pkgNode.nodeName) {
                    "uses-permission", "permission" -> {
                        parsePermission(pkgNode, out)
                    }

                    "uses-feature" -> {
                        parseFeature(pkgNode, out)
                    }

                    "application" -> {
                        val appNodes = pkgNode.childNodes
                        appNodes.forEach { appNode ->
                            when (appNode.nodeName) {
                                "meta-data" -> {
                                    parseMetaData(appNode, out)
                                }

                                "activity" -> {
                                    parseActivity(appNode, out)
                                }

                                "service" -> {
                                    parseService(appNode, out)
                                }

                                "provider" -> {
                                    parseProvider(appNode, out)
                                }

                                "receiver" -> {
                                    parseReceiver(appNode, out)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun parseMetaData(node: Node, out: AppSdkInfo) {
        val pair = parseAttributes(node, ATTRIBUTE_ANDROID_NAME, ATTRIBUTE_ANDROID_VALUE)
        out.check(AppSdkInfo.Type.MetaData, "${pair.first} = ${pair.second}")
    }

    private fun parsePermission(node: Node, out: AppSdkInfo) {
        parseByAndroidName(node, out, AppSdkInfo.Type.Permission)
    }

    private fun parseFeature(node: Node, out: AppSdkInfo) {
        val pair = parseAttributes(node, ATTRIBUTE_ANDROID_NAME, ATTRIBUTE_ANDROID_REQUIRED)
        out.check(AppSdkInfo.Type.MetaData, "${pair.first} = ${pair.second}")
    }

    private fun parseActivity(node: Node, out: AppSdkInfo) {
        parseByAndroidName(node, out, AppSdkInfo.Type.Activity)
    }

    private fun parseProvider(node: Node, out: AppSdkInfo) {
        parseByAndroidName(node, out, AppSdkInfo.Type.Provider)
    }

    private fun parseReceiver(node: Node, out: AppSdkInfo) {
        parseByAndroidName(node, out, AppSdkInfo.Type.Receiver)
    }

    private fun parseService(node: Node, out: AppSdkInfo) {
        parseByAndroidName(node, out, AppSdkInfo.Type.Service)
    }

    private fun parseByAndroidName(node: Node, out: AppSdkInfo, type: AppSdkInfo.Type) {
        val pair = parseAttributes(node, ATTRIBUTE_ANDROID_NAME, ATTRIBUTE_ANDROID_VALUE)
        out.check(type, pair.first)
    }

    private fun parseAttributesMap(
        nodeList: NodeList,
        nameKey: String,
        valueKey: String
    ): Map<String, String> {
        val metaMap = mutableMapOf<String, String>()
        nodeList.forEach { node ->
            val attributes = node.attributes
            if (attributes != null) {
                var metaName = ""
                var metaValue = ""
                for (j in 0 until attributes.length) {
                    val attribute = attributes.item(j)
                    val attrName = attribute.nodeName
                    when (attrName) {
                        nameKey -> {
                            metaName = attribute.nodeValue
                        }

                        valueKey -> {
                            metaValue = attribute.nodeValue
                        }
                    }
                }
                if (metaName.isNotEmpty()) {
                    metaMap[metaName] = metaValue
                }
            }
        }
        return metaMap
    }

    private fun parseAttributes(
        node: Node,
        nameKey: String,
        valueKey: String
    ): Pair<String, String> {
        val attributes = node.attributes
        var metaName = ""
        var metaValue = ""
        if (attributes != null) {
            for (j in 0 until attributes.length) {
                val attribute = attributes.item(j)
                val attrName = attribute.nodeName
                when (attrName) {
                    nameKey -> {
                        metaName = attribute.nodeValue
                    }

                    valueKey -> {
                        metaValue = attribute.nodeValue
                    }
                }
            }
        }
        return metaName to metaValue
    }

    private fun NodeList.forEach(block: (Node) -> Unit) {
        for (i in 0 until length) {
            val node = item(i) ?: continue
            block(node)
        }
    }

}