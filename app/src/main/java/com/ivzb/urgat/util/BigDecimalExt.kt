package com.ivzb.urgat.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

private val NUMBER_FORMAT = NumberFormat.getInstance(Locale.getDefault()).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 2
    isGroupingUsed = true
    roundingMode = RoundingMode.DOWN
}

fun BigDecimal.format(): String = NUMBER_FORMAT.format(this)

fun BigDecimal?.formatPnL(): String = this?.let {
    "${if (this >= BigDecimal.ZERO) "+" else "-"}${this.format()}%"
}.orEmpty()

fun BigDecimal.round(): BigDecimal {
    return this.setScale(2, RoundingMode.HALF_UP)
}

fun BigDecimal.toPercentage(): BigDecimal {
    return this / 100.toBigDecimal()
}

operator fun BigDecimal.div(other: BigDecimal): BigDecimal {
    return this.divide(
        other,
        maxOf(this.scale(), other.scale(), 2),
        RoundingMode.HALF_UP
    )
}