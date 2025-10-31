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

    fun getMaxDaysInMonth(year: Int, month: Int): Int {
        val isLeapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        return when (month) {
            2 -> if (isLeapYear) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
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

fun LocalDateTime.copy(
    year: Int? = null,
    month: Int? = null,
    day: Int? = null,
    hour: Int? = null,
    minute: Int? = null,
    second: Int? = null
): LocalDateTime {
    val newYear = year ?: this.year
    val newMonth = month ?: this.monthNumber
    val newDay = day ?: this.dayOfMonth
    val newHour = hour ?: this.hour
    val newMinute = minute ?: this.minute
    val newSecond = second ?: this.second

    val maxDays = this.getMaxDaysInMonth(newYear, newMonth)
    val correctedDay = newDay.coerceAtMost(maxDays)

    return LocalDateTime(
        year = newYear,
        monthNumber = newMonth,
        dayOfMonth = correctedDay,
        hour = newHour,
        minute = newMinute,
        second = newSecond
    )
}

/**
 * 获取某年某月的最大天数
 */
fun LocalDateTime.getMaxDaysInMonth(year: Int? = null, month: Int? = null): Int {
    val targetYear = year ?: this.year
    val targetMonth = month ?: this.monthNumber

    val isLeapYear = targetYear % 4 == 0 && (targetYear % 100 != 0 || targetYear % 400 == 0)

    return when (targetMonth) {
        2 -> if (isLeapYear) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
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
