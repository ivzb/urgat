package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.expense.ExpenseEntity
import com.ivzb.urgat.data.investment.InvestmentEntity
import com.ivzb.urgat.data.note.NoteEntity
import com.ivzb.urgat.data.todo.TodoEntity
import com.ivzb.urgat.data.weight.WeightEntity
import java.util.UUID

data class CsvMapping(
    val column: String,
    val type: String,
) {

    companion object {

        const val TODOS_CSV_FILE_NAME = "todos.csv"
        const val NOTES_CSV_FILE_NAME = "notes.csv"
        const val EXPENSES_CSV_FILE_NAME = "expenses.csv"
        const val INVESTMENTS_CSV_FILE_NAME = "investments.csv"
        const val WEIGHTS_CSV_FILE_NAME = "weights.csv"

        val TODOS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("title", "string"),
            CsvMapping("description", "string"),
            CsvMapping("isCompleted", "boolean"),
            CsvMapping("createdAt", "long"),
        )

        val NOTES_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("content", "string"),
            CsvMapping("createdAt", "long"),
        )

        val EXPENSES_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("name", "string"),
            CsvMapping("amount", "big_decimal"),
            CsvMapping("category", "string"),
            CsvMapping("date", "long"),
            CsvMapping("excludeFromAnalytics", "boolean"),
            CsvMapping("createdAt", "long"),
        )

        val INVESTMENTS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("name", "string"),
            CsvMapping("shares", "big_decimal"),
            CsvMapping("cost", "big_decimal"),
            CsvMapping("category", "string"),
            CsvMapping("currency", "string"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

        val WEIGHTS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("value", "big_decimal"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

        fun parseTodoCsv(line: String): TodoEntity {
            val split = splitCsvLine(line)

            return TodoEntity(
                id = split[0].ifEmpty { UUID.randomUUID().toString() },
                title = split[1],
                description = split[2],
                isCompleted = split[3].toBoolean(),
                isDeleted = false,
                createdAt = split[4].toLong(),
            )
        }

        fun parseNoteCsv(line: String): NoteEntity {
            val split = splitCsvLine(line)

            return NoteEntity(
                id = split[0].ifEmpty { UUID.randomUUID().toString() },
                content = split[1],
                isDeleted = false,
                createdAt = split[2].toLong(),
            )
        }

        fun parseExpenseCsv(line: String): ExpenseEntity {
            val split = splitCsvLine(line)

            return ExpenseEntity(
                id = split[0].ifEmpty { UUID.randomUUID().toString() },
                name = split[1],
                amount = split[2].toBigDecimal(),
                category = split[3],
                date = split[4].toLong(),
                excludeFromAnalytics = split[5].toBoolean(),
                isDeleted = false,
                createdAt = split[6].toLong(),
            )
        }

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

        private const val SEPARATOR = ", "

        fun List<CsvMapping>.format(): String {
            return this.joinToString(SEPARATOR) { it.column }
        }

        // handles commas inside quotes
        private fun splitCsvLine(line: String): List<String> {
            val result = mutableListOf<String>()
            val current = StringBuilder()
            var inQuotes = false

            for (char in line) {
                when {
                    char == '"' -> inQuotes = !inQuotes

                    char == ',' && !inQuotes -> {
                        result.add(current.toString())
                        current.clear()
                    }

                    else -> current.append(char)
                }
            }

            result.add(current.toString())
            return result
        }

    }
}