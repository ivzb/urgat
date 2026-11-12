package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.budget.BudgetEntity
import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import java.util.UUID

object BudgetCsvParser {

    fun parseBudgetCsv(line: String): BudgetEntity {
        val split = splitCsvLine(line)

        return BudgetEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            income = split[1].toBigDecimal(),
            bankStart = split[2].toBigDecimal(),
            bankEnd = split[3].toBigDecimalOrNull(),
            currency = split[4],
            date = split[5].toLong(),
            isDeleted = false,
            createdAt = split[6].toLong(),
        )
    }

}
