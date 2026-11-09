package com.ivzb.urgat.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.cos

private val NUMBER_FORMAT = NumberFormat.getInstance(Locale.getDefault()).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 2
    isGroupingUsed = true
    roundingMode = RoundingMode.DOWN
}

fun BigDecimal.format(digits: Int? = null): String = NUMBER_FORMAT.apply {
    minimumFractionDigits = digits ?: 0
    maximumFractionDigits = digits ?: 2
}.format(this)

fun BigDecimal?.formatPnL(digits: Int? = null, withPercentageSign: Boolean = true): String = this?.let {
    "${if (this >= BigDecimal.ZERO) "+" else ""}${this.format(digits)}${if (withPercentageSign) "%" else ""}"
}.orEmpty()

// signum, not ==, as BigDecimal equality is scale sensitive (0.00 != 0)
fun BigDecimal.isZero(): Boolean = signum() == 0

fun calculatePnL(costPrice: BigDecimal, marketPrice: BigDecimal?): BigDecimal? {
    if (costPrice.isZero() || marketPrice == null) {
        return null
    }

    if (marketPrice == costPrice) {
        return BigDecimal.ZERO
    }

    return (marketPrice - costPrice) / costPrice * BigDecimal(100)
}

fun calculateFuelConsumption(fuelAmount: BigDecimal, distance: Int?): BigDecimal? {
    if (distance == null || distance <= 0) {
        return null
    }

    return fuelAmount * 100.toBigDecimal() / distance.toBigDecimal()
}

fun BigDecimal.round(): BigDecimal = this.setScale(2, RoundingMode.HALF_UP)

fun BigDecimal.toPercentage(): BigDecimal = this / 100.toBigDecimal()

operator fun BigDecimal.div(other: BigDecimal): BigDecimal = this.divide(
    other,
    maxOf(this.scale(), other.scale(), 2),
    RoundingMode.HALF_UP
)
