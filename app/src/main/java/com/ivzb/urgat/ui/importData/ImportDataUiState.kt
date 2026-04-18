package com.ivzb.urgat.ui.importData

import com.ivzb.urgat.data.csv.CsvMapping
import com.ivzb.urgat.data.csv.CsvMapping.Companion.BUDGETS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.FUEL_CONSUMPTION_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.SAVINGS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WATCHLIST_ASSETS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.CAR_MAINTENANCE_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_MAPPING
import com.ivzb.urgat.data.source.sample.SampleData

data class ImportDataUiState(
    val isManualMode: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,

    val manualDataMapping: Map<ImportDataPage, List<CsvMapping>> = mapOf(
        ImportDataPage.Expenses to EXPENSES_CSV_MAPPING,
        ImportDataPage.Investments to INVESTMENTS_CSV_MAPPING,
        ImportDataPage.Savings to SAVINGS_CSV_MAPPING,
        ImportDataPage.Budgets to BUDGETS_CSV_MAPPING,
        ImportDataPage.Todos to TODOS_CSV_MAPPING,
        ImportDataPage.Notes to NOTES_CSV_MAPPING,
        ImportDataPage.WatchlistAssets to WATCHLIST_ASSETS_CSV_MAPPING,
        ImportDataPage.FuelConsumption to FUEL_CONSUMPTION_CSV_MAPPING,
        ImportDataPage.CarMaintenance to CAR_MAINTENANCE_CSV_MAPPING,
        ImportDataPage.Weights to WEIGHTS_CSV_MAPPING,
    ),

    val manualDataExample: Map<ImportDataPage, String> = mapOf(
        ImportDataPage.Expenses to SampleData.SAMPLE_EXPENSES[0].formatCsv(),
        ImportDataPage.Investments to SampleData.SAMPLE_INVESTMENTS[0].formatCsv(),
        ImportDataPage.Savings to SampleData.SAMPLE_SAVINGS[0].formatCsv(),
        ImportDataPage.Budgets to SampleData.SAMPLE_BUDGETS[0].formatCsv(),
        ImportDataPage.Todos to SampleData.SAMPLE_TODOS[0].formatCsv(),
        ImportDataPage.Notes to SampleData.SAMPLE_NOTES[0].formatCsv(),
        ImportDataPage.WatchlistAssets to SampleData.SAMPLE_WATCHLIST_ASSETS[0].formatCsv(),
        ImportDataPage.FuelConsumption to SampleData.SAMPLE_FUEL_CONSUMPTION[0].formatCsv(),
        ImportDataPage.CarMaintenance to SampleData.SAMPLE_CAR_MAINTENANCE[0].formatCsv(),
        ImportDataPage.Weights to SampleData.SAMPLE_WEIGHTS[0].formatCsv(),
    ),

    val manualDataContent: Map<ImportDataPage, String> = mapOf(),

    val initialPage: Int = 0,
)