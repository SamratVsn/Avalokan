package com.example.avalokan.data

import androidx.room3.ColumnTypeConverter

class Converters {

    @ColumnTypeConverter
    fun fromStringList(values: List<String>?): String =
        values.orEmpty().joinToString(SEPARATOR.toString()) { escape(it) }

    @ColumnTypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(SEPARATOR).map { unescape(it) }
    }

    private fun escape(value: String): String = buildString(value.length) {
        value.forEach { char ->
            when (char) {
                ESCAPE -> append("%25")
                SEPARATOR -> append("%2C")
                else -> append(char)
            }
        }
    }

    private fun unescape(value: String): String = buildString(value.length) {
        var index = 0
        while (index < value.length) {
            val char = value[index]
            if (char == ESCAPE && index + 2 < value.length) {
                when (value.substring(index + 1, index + 3)) {
                    "25" -> {
                        append(ESCAPE)
                        index += 3
                        continue
                    }
                    "2C" -> {
                        append(SEPARATOR)
                        index += 3
                        continue
                    }
                }
            }
            append(char)
            index++
        }
    }

    private companion object {
        const val SEPARATOR = ','
        const val ESCAPE = '%'
    }
}
