package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.csv.BudgetCsvParser.parseBudgetCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.BUDGETS_CSV_FILE_NAME
import com.ivzb.urgat.data.importData.ImportDataRepository
import javax.inject.Inject

interface ImportBudgetsUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importBudgets(content: String)

}

class DefaultImportBudgetsUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val budgetDao: BudgetDao,
): ImportBudgetsUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, BUDGETS_CSV_FILE_NAME, ::parseBudgetCsv, budgetDao::insertAll)
    }

    override suspend fun importBudgets(content: String) {
        importDataRepository.importString(content, ::parseBudgetCsv, budgetDao::insertAll)
    }

}
