package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionEntity
import java.util.UUID

object FuelConsumptionCsvParser {

    fun parseFuelConsumptionCsv(line: String): FuelConsumptionEntity {
        val split = splitCsvLine(line)

        return FuelConsumptionEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            amount = split[1].toBigDecimal(),
            price = split[2].toBigDecimal(),
            currency = split[3],
            type = split[4],
            carName = split[5],
            odometer = split[6].toInt(),
            date = split[7].toLong(),
            isDeleted = false,
            createdAt = split[8].toLong(),
        )
    }

}
