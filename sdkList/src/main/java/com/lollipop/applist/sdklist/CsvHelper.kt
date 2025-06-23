package com.lollipop.applist.sdklist

object CsvHelper {

    @JvmStatic
    fun build(vararg title: String): Builder {
        val builder = Builder()
        if (title.isNotEmpty()) {
            builder.addLine(*title)
        }
        return builder
    }

    class Builder {

        private val rows = mutableListOf<Row>()

        fun addLine(row: Row?) {
            row ?: return
            rows.add(row)
        }

        fun addLine(vararg items: String) {
            rows.add(Row(items))
        }

        fun build(): String {
            val builder = StringBuilder()
            rows.forEach {
                it.itemArray.forEachIndexed { index, item ->
                    builder.append("\"")
                    builder.append(item.replace("\"", "\"\""))
                    builder.append("\"")
                    if (index < it.itemArray.size - 1) {
                        builder.append(",")
                    }
                }
                builder.append("\r\n")
            }
            return builder.toString()
        }
    }


    class Row(
        val itemArray: Array<out String>
    )

}