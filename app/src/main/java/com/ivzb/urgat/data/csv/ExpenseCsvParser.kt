package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.expense.ExpenseEntity
import java.util.UUID

object ExpenseCsvParser {

    fun parseExpenseCsv(line: String): ExpenseEntity {
        val split = splitCsvLine(line)

        return ExpenseEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            name = split[1],
            amount = split[2].toBigDecimal(),
            currency = split[3],
            category = split[4],
            date = split[5].toLong(),
            excludeFromAnalytics = split[6].toBoolean(),
            isDeleted = false,
            createdAt = split[7].toLong(),
        )
    }

}
