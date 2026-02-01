package com.ivzb.urgat.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun LocalDate.toTimestamp(): Long {
    return this
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun LocalDate.format(): String {
    return this.format(DATE_FORMATTER)
}

fun currentMonthRange(): LongRange {
    val zoneId = ZoneId.systemDefault()
    val now = ZonedDateTime.ofInstant(Instant.now(), zoneId)

    val startOfMonth = now
        .withDayOfMonth(1)
        .toLocalDate()
        .atStartOfDay(zoneId)
        .toInstant()

    val endOfMonth = now
        .withDayOfMonth(now.toLocalDate().lengthOfMonth())
        .toLocalDate()
        .atTime(23, 59, 59, 999_000_000)
        .atZone(zoneId)
        .toInstant()

    return startOfMonth.toEpochMilli() until endOfMonth.toEpochMilli()
}

fun untilCurrentMonthRange(): LongRange {
    val zoneId = ZoneId.systemDefault()
    val now = ZonedDateTime.ofInstant(Instant.now(), zoneId)

    val previousMonth = now.minusMonths(1)

    val endOfPreviousMonth = previousMonth
        .withDayOfMonth(previousMonth.toLocalDate().lengthOfMonth())
        .toLocalDate()
        .atTime(23, 59, 59, 999_000_000)
        .atZone(zoneId)
        .toInstant()

    return 0 until endOfPreviousMonth.toEpochMilli()
}

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
