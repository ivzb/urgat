package com.ivzb.urgat.domain.exportData

import android.database.Cursor
import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_MAPPING
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.exportData.ExportDataRepository
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.weight.WeightDao
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class DefaultExportDataUseCaseTest {

    private lateinit var exportDataRepository: ExportDataRepository
    private lateinit var todoDao: TodoDao
    private lateinit var noteDao: NoteDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var investmentDao: InvestmentDao
    private lateinit var weightDao: WeightDao
    private val dispatcher = StandardTestDispatcher()

    private lateinit var exportDataUseCase: ExportDataUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        exportDataRepository = mockk(relaxed = true)
        todoDao = mockk(relaxed = true)
        noteDao = mockk(relaxed = true)
        expenseDao = mockk(relaxed = true)
        investmentDao = mockk(relaxed = true)
        weightDao = mockk(relaxed = true)

        exportDataUseCase = DefaultExportDataUseCase(
            exportDataRepository,
            todoDao,
            noteDao,
            expenseDao,
            investmentDao,
            weightDao,
            dispatcher,
        )
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val uri = mockk<Uri>()

        val todosCursor = mockk<Cursor>()
        every { todoDao.stream() } returns todosCursor

        val notesCursor = mockk<Cursor>()
        every { noteDao.stream() } returns notesCursor

        val expensesCursor = mockk<Cursor>()
        every { expenseDao.stream() } returns expensesCursor

        val investmentsCursor = mockk<Cursor>()
        every { investmentDao.stream() } returns investmentsCursor

        val weightsCursor = mockk<Cursor>()
        every { weightDao.stream() } returns weightsCursor

        exportDataUseCase(uri)

        coVerify(exactly = 1) {
            exportDataRepository.exportCsv(eq(uri), eq(todosCursor), eq("todos.csv"), eq(TODOS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(notesCursor), eq("notes.csv"), eq(NOTES_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(expensesCursor), eq("expenses.csv"), eq(EXPENSES_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(investmentsCursor), eq("investments.csv"), eq(INVESTMENTS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(weightsCursor), eq("weights.csv"), eq(WEIGHTS_CSV_MAPPING))
        }
    }
}
