package com.ivzb.urgat.util

import java.time.LocalDate

fun timestamp(year: Int, month: Int, day: Int) = LocalDate.of(year, month, day).toTimestamp()
