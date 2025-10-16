package io.github.hcisme.note.utils

object DateUtil {
    val shortWeekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val months = listOf(
        "一月",
        "二月",
        "三月",
        "四月",
        "五月",
        "六月",
        "七月",
        "八月",
        "九月",
        "十月",
        "十一月",
        "十二月"
    )

    fun yearsAround(year: Int, spanEachSide: Int = 5): List<Int> {
        return (year - spanEachSide..year + spanEachSide).toList()
    }
}