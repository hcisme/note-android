package io.github.hcisme.note.utils

import io.github.hcisme.note.enums.DateTimePatternEnum
import kotlinx.datetime.LocalDateTime

object DateUtil {
    val shortWeekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

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

fun LocalDateTime.formatWithPattern(pattern: DateTimePatternEnum = DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS): String {
    val yearStr = year.toString()
    val monthStr = monthNumber.toString().padStart(2, '0')
    val dayStr = dayOfMonth.toString().padStart(2, '0')
    val hourStr = hour.toString().padStart(2, '0')
    val minuteStr = minute.toString().padStart(2, '0')
    val secondStr = second.toString().padStart(2, '0')

    return when (pattern) {
        DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS -> "$yearStr-$monthStr-$dayStr $hourStr:$minuteStr:$secondStr"
        DateTimePatternEnum.YYYY_MM_DD_HH_MM -> "$yearStr-$monthStr-$dayStr $hourStr:$minuteStr"
        DateTimePatternEnum.YYYY_MM_DD_HH -> "$yearStr-$monthStr-$dayStr $hourStr"
        DateTimePatternEnum.YYYY_MM_DD -> "$yearStr-$monthStr-$dayStr"
        DateTimePatternEnum.YYYY_MM -> "$yearStr-$monthStr"
        DateTimePatternEnum.YYYY -> yearStr
    }
}

fun String.toLocalDateTime(): LocalDateTime {
    val parts = this.split(" ", "-", ":")
    require(parts.size == 6) { "Invalid date format: $this" }

    return LocalDateTime(
        year = parts[0].toInt(),
        monthNumber = parts[1].toInt(),
        dayOfMonth = parts[2].toInt(),
        hour = parts[3].toInt(),
        minute = parts[4].toInt(),
        second = parts[5].toInt()
    )
}
