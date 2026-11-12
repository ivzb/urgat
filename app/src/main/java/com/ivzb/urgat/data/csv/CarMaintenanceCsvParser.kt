package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.carMaintenance.CarMaintenanceEntity
import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import java.util.UUID

object CarMaintenanceCsvParser {

    fun parseCarMaintenanceCsv(line: String): CarMaintenanceEntity {
        val split = splitCsvLine(line)

        return CarMaintenanceEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            description = split[1],
            partNumber = split[2],
            url = split[3].ifEmpty { null },
            carName = split[4],
            odometer = split[5].toInt(),
            date = split[6].toLong(),
            isDeleted = false,
            createdAt = split[7].toLong(),
        )
    }

}
