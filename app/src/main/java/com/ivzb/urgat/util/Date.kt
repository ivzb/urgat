package com.ivzb.urgat.util

import android.content.Context
import android.text.format.DateUtils
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

/**
 * The clock time of the given moment - "14:32", or "3 Oct, 14:32" once it is no longer today.
 *
 * Deliberately absolute rather than "5 min. ago". A home screen widget is only ever redrawn when
 * something asks it to, so a relative wording freezes at whatever it said when it was drawn and
 * then quietly lies - every widget claiming to have been refreshed "just now" for the next half
 * hour. A clock time drawn once stays true for as long as it is on screen.
 *
 * Follows the user's 12/24 hour setting, because the system formatter does.
 */
fun Context.formatUpdatedAt(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val sameDay = timestamp.toLocalDate() == now.toLocalDate()

    val flags = DateUtils.FORMAT_SHOW_TIME or if (sameDay) {
        0
    } else {
        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_NO_YEAR
    }

    return DateUtils.formatDateTime(this, timestamp, flags)
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

fun lastMonthRange(): LongRange {
    val zoneId = ZoneId.systemDefault()
    val now = ZonedDateTime.ofInstant(Instant.now(), zoneId)

    val previousMonth = now.minusMonths(1)

    val startOfPreviousMonth = previousMonth
        .withDayOfMonth(1)
        .toLocalDate()
        .atStartOfDay(zoneId)
        .toInstant()

    val endOfPreviousMonth = previousMonth
        .withDayOfMonth(previousMonth.toLocalDate().lengthOfMonth())
        .toLocalDate()
        .atTime(23, 59, 59, 999_000_000)
        .atZone(zoneId)
        .toInstant()

    return startOfPreviousMonth.toEpochMilli() until endOfPreviousMonth.toEpochMilli()
}

fun yearToDateRange(): LongRange {
    val zoneId = ZoneId.systemDefault()
    val now = ZonedDateTime.ofInstant(Instant.now(), zoneId)

    val startOfYear = now
        .withMonth(1)
        .withDayOfMonth(1)
        .toLocalDate()
        .atStartOfDay(zoneId)
        .toInstant()

    val previousMonth = now.minusMonths(1)

    val endOfPreviousMonth = previousMonth
        .withDayOfMonth(previousMonth.toLocalDate().lengthOfMonth())
        .toLocalDate()
        .atTime(23, 59, 59, 999_000_000)
        .atZone(zoneId)
        .toInstant()

    return startOfYear.toEpochMilli() until endOfPreviousMonth.toEpochMilli()
}

fun lastYearRange(): LongRange {
    val zoneId = ZoneId.systemDefault()
    val now = ZonedDateTime.ofInstant(Instant.now(), zoneId)

    val lastYear = now.minusYears(1)

    val startOfLastYear = lastYear
        .withMonth(1)
        .withDayOfMonth(1)
        .toLocalDate()
        .atStartOfDay(zoneId)
        .toInstant()

    val endOfLastYear = lastYear
        .withMonth(12)
        .withDayOfMonth(31)
        .toLocalDate()
        .atTime(23, 59, 59, 999_000_000)
        .atZone(zoneId)
        .toInstant()

    return startOfLastYear.toEpochMilli() until endOfLastYear.toEpochMilli()
}

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
