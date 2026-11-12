package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.note.NoteEntity
import java.util.UUID

object NoteCsvParser {

    fun parseNoteCsv(line: String): NoteEntity {
        val split = splitCsvLine(line)

        return NoteEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            content = split[1],
            isDeleted = false,
            createdAt = split[2].toLong(),
        )
    }

}
