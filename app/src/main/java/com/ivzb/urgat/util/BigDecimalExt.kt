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