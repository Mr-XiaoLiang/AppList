package com.lollipop.applist.sdklist

sealed class SDK(
    val label: String,
    val website: String,
    val keywords: List<String>
) {

    class ADS(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords)

    class Track(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords)

    class Share(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords)

    class Other(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords)

    val keywordsString: String by lazy {
        keywords.joinToString(", ")
    }

    fun isMatch(text: String): Boolean {
        if (keywords.isEmpty()) {
            return false
        }
        val result = keywords.any {
            text.contains(it, ignoreCase = true)
        }
        return result
    }

}