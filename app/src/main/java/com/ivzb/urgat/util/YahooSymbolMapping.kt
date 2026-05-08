package com.ivzb.urgat.util

fun normalizeInstrument(symbol: String): String =
    yahooSymbolMapping[symbol] ?: symbol

// replace user symbol with yahoo finance symbol
private val yahooSymbolMapping = mapOf(
    "BRK.B" to "BRK-B",
    "BRK B" to "BRK-B",
    "SXR8" to "SXR8.DE",
    "SXR.8" to "SXR8.DE",
    "SXR 8" to "SXR8.DE",
    "XAU" to "GC=F",
    "BTC" to "BTC-EUR",
    "ETH" to "ETH-EUR",
    "$" to "USD",
    "€" to "EUR",
    "€$=X" to "EURUSD=X",
    "MEUD" to "MEUD.MI",
)