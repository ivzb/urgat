package com.ivzb.urgat.domain.exportData

import android.net.Uri
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_MAPPING
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.exportData.ExportDataRepository
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.data.weight.WeightDao
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ExportDataUseCase {

    suspend operator fun invoke(uri: Uri)

}

class DefaultExportDataUseCase @Inject constructor(
    private val exportDataRepository: ExportDataRepository,
    private val todoDao: TodoDao,
    private val noteDao: NoteDao,
    private val expenseDao: ExpenseDao,
    private val investmentDao: InvestmentDao,
    private val weightDao: WeightDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): ExportDataUseCase {

    override suspend operator fun invoke(uri: Uri) {
        return withContext(dispatcher) {
            exportDataRepository.exportCsv(uri, todoDao.stream(), TODOS_CSV_FILE_NAME, TODOS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, noteDao.stream(), NOTES_CSV_FILE_NAME, NOTES_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, expenseDao.stream(), EXPENSES_CSV_FILE_NAME, EXPENSES_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, investmentDao.stream(), INVESTMENTS_CSV_FILE_NAME, INVESTMENTS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, weightDao.stream(), WEIGHTS_CSV_FILE_NAME, WEIGHTS_CSV_MAPPING)
        }
    }

}
