package com.ivzb.urgat.data.marketPrice

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YahooFinanceApi {

    @GET("v8/finance/chart/{symbol}")
    suspend fun fetch(
        @Path("symbol") symbol: String,
        @Query("interval") interval: String = "1d",
        @Query("range") range: String = "1d",
    ): YahooFinanceResponse

    companion object {

        const val BASE_URL = "https://query1.finance.yahoo.com/"

    }

}

