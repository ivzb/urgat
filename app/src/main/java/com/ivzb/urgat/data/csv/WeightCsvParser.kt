package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.weight.WeightEntity
import java.util.UUID

object WeightCsvParser {

    fun parseWeightCsv(line: String): WeightEntity {
        val split = splitCsvLine(line)

        return WeightEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            value = split[1].toBigDecimal(),
            date = split[2].toLong(),
            isDeleted = false,
            createdAt = split[3].toLong(),
        )
    }

}
