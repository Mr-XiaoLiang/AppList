package com.lollipop.applist.sdklist

class AdKeyword(private val mapImpl: Map<String, AdType>) : Map<String, AdType> by mapImpl {

    companion object {
        fun create(block: Builder.() -> Unit): AdKeyword {
            val builder = Builder()
            block(builder)
            return builder.build()
        }
    }

    fun match(keyword: String): AdType {
        return mapImpl[keyword] ?: AdType.OTHER
    }

    class Builder {

        private val map = mutableMapOf<String, AdType>()

        fun add(keyword: String, type: AdType) {
            map[keyword] = type
        }

        fun build(): AdKeyword {
            return AdKeyword(map)
        }

    }

}