package com.lollipop.applist.sdklist

sealed class SDK(
    val label: String,
    val website: String,
    val keywords: List<String>
) {

    abstract val color: Int
    abstract val typeName: String

    class ADS(
        label: String,
        website: String,
        keywords: List<String>,
        val adKeyword: AdKeyword
    ) : SDK(label, website, keywords) {
        override val color: Int = 0xFF48990F.toInt()
        override val typeName: String = "ADS"
    }

    class Track(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords) {
        override val color: Int = 0xFF0F9969.toInt()
        override val typeName: String = "Track"
    }

    class Pay(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords) {
        override val color: Int = 0xFFC83D76.toInt()
        override val typeName: String = "Pay"
    }

    class Share(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords) {
        override val color: Int = 0xFF750F99.toInt()
        override val typeName: String = "Share"
    }

    class Other(
        label: String,
        website: String,
        keywords: List<String>
    ) : SDK(label, website, keywords) {
        override val color: Int = 0xFFB8C83D.toInt()
        override val typeName: String = "Other"
    }

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