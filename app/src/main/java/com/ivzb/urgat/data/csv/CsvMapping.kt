package com.ivzb.urgat.data.csv

import com.ivzb.urgat.data.budget.BudgetEntity
import com.ivzb.urgat.data.expense.ExpenseEntity
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionEntity
import com.ivzb.urgat.data.investment.InvestmentEntity
import com.ivzb.urgat.data.note.NoteEntity
import com.ivzb.urgat.data.saving.SavingEntity
import com.ivzb.urgat.data.todo.TodoEntity
import com.ivzb.urgat.data.watchlist.WatchlistAssetEntity
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
        const val SAVINGS_CSV_FILE_NAME = "savings.csv"
        const val BUDGETS_CSV_FILE_NAME = "budgets.csv"
        const val WATCHLIST_ASSETS_CSV_FILE_NAME = "watchlist_assets.csv"
        const val WEIGHTS_CSV_FILE_NAME = "weights.csv"
        const val FUEL_CONSUMPTION_CSV_FILE_NAME = "fuel_consumption.csv"

        val TODOS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("content", "string"),
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
            CsvMapping("currency", "string"),
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

        val SAVINGS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("name", "string"),
            CsvMapping("amount", "big_decimal"),
            CsvMapping("currency", "string"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

        val BUDGETS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("income", "big_decimal"),
            CsvMapping("bankStart", "big_decimal"),
            CsvMapping("bankEnd", "big_decimal?"),
            CsvMapping("currency", "string"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

        val WATCHLIST_ASSETS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("name", "string"),
            CsvMapping("createdAt", "long"),
        )

        val WEIGHTS_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("value", "big_decimal"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

        val FUEL_CONSUMPTION_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("amount", "big_decimal"),
            CsvMapping("price", "big_decimal"),
            CsvMapping("currency", "string"),
            CsvMapping("type", "string"),
            CsvMapping("carName", "string"),
            CsvMapping("odometer", "int"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

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
                currency = split[3],
                category = split[4],
                date = split[5].toLong(),
                excludeFromAnalytics = split[6].toBoolean(),
                isDeleted = false,
                createdAt = split[7].toLong(),
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

        fun parseSavingCsv(line: String): SavingEntity {
            val split = splitCsvLine(line)

            return SavingEntity(
                id = split[0].ifEmpty { UUID.randomUUID().toString() },
                name = split[1],
                amount = split[2].toBigDecimal(),
                currency = split[3],
                date = split[4].toLong(),
                isDeleted = false,
                createdAt = split[5].toLong(),
            )
        }

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

        fun parseWatchlistAssetCsv(line: String): WatchlistAssetEntity {
            val split = splitCsvLine(line)

            return WatchlistAssetEntity(
                id = split[0].ifEmpty { UUID.randomUUID().toString() },
                name = split[1],
                isDeleted = false,
                createdAt = split[2].toLong(),
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
                        result.add(current.trim().toString())
                        current.clear()
                    }

                    else -> current.append(char)
                }
            }

            result.add(current.trim().toString())
            return result
        }

    }
}