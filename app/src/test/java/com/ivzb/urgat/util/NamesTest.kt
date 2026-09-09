package com.ivzb.urgat.util

import org.junit.Test
import kotlin.test.assertEquals

class NamesTest {

    @Test
    fun `formatNames should return nothing when there is nothing to name`() {
        assertEquals("", format(emptyList(), limit = 3))
    }

    @Test
    fun `formatNames should list every name when they all fit`() {
        assertEquals("AAPL, TSLA", format(listOf("AAPL", "TSLA"), limit = 3))
    }

    @Test
    fun `formatNames should list every name when they exactly fill the limit`() {
        assertEquals("AAPL, TSLA, NVDA", format(listOf("AAPL", "TSLA", "NVDA"), limit = 3))
    }

    @Test
    fun `formatNames should count the names it has no room for`() {
        // a widget is a few centimetres wide, so a portfolio which lost every quote has to be
        // summarised rather than spelled out and clipped mid symbol
        assertEquals(
            "AAPL, TSLA, NVDA and 2 more",
            format(listOf("AAPL", "TSLA", "NVDA", "VOO", "META"), limit = 3),
        )
    }

    @Test
    fun `formatNames should still name one when there is no room for any`() {
        // counting alone says no more than the generic message this is meant to improve on
        assertEquals("AAPL and 1 more", format(listOf("AAPL", "TSLA"), limit = 0))
    }

    @Test
    fun `formatNames should keep the order it was given`() {
        // the caller sorts, so the reader must not shuffle the names underneath it
        assertEquals("TSLA, AAPL", format(listOf("TSLA", "AAPL"), limit = 3))
    }

    private fun format(names: List<String>, limit: Int): String =
        formatNames(names, limit) { shown, remaining -> "$shown and $remaining more" }

}
