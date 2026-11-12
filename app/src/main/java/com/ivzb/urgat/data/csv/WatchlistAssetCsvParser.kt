package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.watchlist.WatchlistAssetEntity
import java.util.UUID

object WatchlistAssetCsvParser {

    fun parseWatchlistAssetCsv(line: String): WatchlistAssetEntity {
        val split = splitCsvLine(line)

        return WatchlistAssetEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            name = split[1],
            isDeleted = false,
            createdAt = split[2].toLong(),
        )
    }

}
