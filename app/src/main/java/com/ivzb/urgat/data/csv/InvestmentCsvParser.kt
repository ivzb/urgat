package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.investment.InvestmentEntity
import java.util.UUID

object InvestmentCsvParser {

    fun parseInvestmentCsv(line: String): InvestmentEntity {
        val split = splitCsvLine(line)

        return InvestmentEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            name = split[1],
            shares = split[2].toBigDecimal(),
            cost = split[3].toBigDecimal(),
            category = split[4],
            currency = split[5],
            date = split[6].toLong(),
            isDeleted = false,
            createdAt = split[7].toLong(),
        )
    }

}
