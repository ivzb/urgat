package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.trade.TradeEntity
import java.util.UUID

object TradeCsvParser {

    fun parseTradeCsv(line: String): TradeEntity {
        val split = splitCsvLine(line)

        return TradeEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            name = split[1],
            amount = split[2].toBigDecimal(),
            currency = split[3],
            date = split[4].toLong(),
            isDeleted = false,
            createdAt = split[5].toLong(),
        )
    }

}
