package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.ExpenseCsvParser.parseExpenseCsv
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.importData.ImportDataRepository
import javax.inject.Inject

interface ImportExpensesUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importExpenses(content: String)

}

class DefaultImportExpensesUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val expenseDao: ExpenseDao,
): ImportExpensesUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, EXPENSES_CSV_FILE_NAME, ::parseExpenseCsv, expenseDao::insertAll)
    }

    override suspend fun importExpenses(content: String) {
        importDataRepository.importString(content, ::parseExpenseCsv, expenseDao::insertAll)
    }

}
