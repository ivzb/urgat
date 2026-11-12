package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.csv.CsvMapping.Companion.splitCsvLine
import com.ivzb.urgat.data.todo.TodoEntity
import java.util.UUID

object TodoCsvParser {

    fun parseTodoCsv(line: String): TodoEntity {
        val split = splitCsvLine(line)

        return TodoEntity(
            id = split[0].ifEmpty { UUID.randomUUID().toString() },
            content = split[1],
            isCompleted = split[2].toBoolean(),
            isDeleted = false,
            createdAt = split[3].toLong(),
        )
    }

}
