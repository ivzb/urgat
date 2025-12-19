package com.ivzb.urgat.util

import kotlin.test.Test
import kotlin.test.assertEquals

class BigDecimalExt {

    @Test
    fun formatDecimalTest() {
        mapOf(
            "12" to "12",

            "12" to "12.0",
            "12.3" to "12.3",
            "12.5" to "12.5",
            "12.9" to "12.9",

            "12" to "12.00",
            "12.01" to "12.01",
            "12.09" to "12.09",
            "12.1" to "12.10",
            "12.11" to "12.11",
            "12.2" to "12.20",
            "12.21" to "12.21",
            "12.34" to "12.34",
            "12.49" to "12.49",
            "12.5" to "12.50",
            "12.51" to "12.51",
            "12.99" to "12.99",

            "12" to "12.000",
            "12" to "12.001",
            "12" to "12.009",
            "12.01" to "12.010",
            "12.01" to "12.011",
            "12.01" to "12.019",
            "12.02" to "12.020",
            "12.09" to "12.099",
            "12.1" to "12.100",
        ).forEach { (expected, value) ->
            assertEquals(expected, value.toBigDecimal().format())
        }
    }

    @Test
    fun formatSeparatorTest() {
        mapOf(
            "123,456,789" to "123456789",

            "12" to "12.0",
            "12.3" to "12.3",
            "12.5" to "12.5",
            "12.9" to "12.9",

            "12" to "12.00",
            "12.01" to "12.01",
            "12.09" to "12.09",
            "12.1" to "12.10",
            "12.11" to "12.11",
            "12.2" to "12.20",
            "12.21" to "12.21",
            "12.34" to "12.34",
            "12.49" to "12.49",
            "12.5" to "12.50",
            "12.51" to "12.51",
            "12.99" to "12.99",

            "12" to "12.000",
            "12" to "12.001",
            "12" to "12.009",
            "12.01" to "12.010",
            "12.01" to "12.011",
            "12.01" to "12.019",
            "12.02" to "12.020",
            "12.09" to "12.099",
            "12.1" to "12.100",
        ).forEach { (expected, value) ->
            assertEquals(expected, value.toBigDecimal().format())
        }
    }

}