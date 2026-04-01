package com.ivzb.urgat.data.marketPrice

import kotlinx.serialization.Serializable

@Serializable
data class YahooFinanceResponse(
    val chart: Chart,
) {

    @Serializable
    data class Chart(
        val result: List<Result>,
    )

    @Serializable
    data class Result(
        val meta: Meta,
    )

    @Serializable
    data class Meta(
        val regularMarketPrice: Double,
        val currency: String,
        val shortName: String,
        val chartPreviousClose: Double,
        val priceHint: Int,
    )
}

