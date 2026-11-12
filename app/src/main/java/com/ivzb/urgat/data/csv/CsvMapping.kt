package com.ivzb.urgat.data.csv

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
        const val CAR_MAINTENANCE_CSV_FILE_NAME = "car_maintenance.csv"
        const val TRADES_CSV_FILE_NAME = "trades.csv"

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

        val CAR_MAINTENANCE_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("description", "string"),
            CsvMapping("partNumber", "string?"),
            CsvMapping("url", "string?"),
            CsvMapping("carName", "string"),
            CsvMapping("odometer", "int"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

        val TRADES_CSV_MAPPING = listOf(
            CsvMapping("id", "string"),
            CsvMapping("name", "string"),
            CsvMapping("amount", "big_decimal"),
            CsvMapping("currency", "string"),
            CsvMapping("date", "long"),
            CsvMapping("createdAt", "long"),
        )

        private const val SEPARATOR = ", "

        fun List<CsvMapping>.format(): String = this.joinToString(SEPARATOR) { it.column }

        // handles commas inside quotes
        internal fun splitCsvLine(line: String): List<String> {
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
