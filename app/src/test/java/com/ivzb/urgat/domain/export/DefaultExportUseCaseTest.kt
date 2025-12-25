package com.ivzb.urgat.domain.export

import android.database.Cursor
import android.net.Uri
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.export.ExportRepository
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.domain.export.DefaultExportUseCase.Companion.EXPENSES_COLUMNS_MAPPING
import com.ivzb.urgat.domain.export.DefaultExportUseCase.Companion.INVESTMENTS_COLUMNS_MAPPING
import com.ivzb.urgat.domain.export.DefaultExportUseCase.Companion.NOTES_COLUMNS_MAPPING
import com.ivzb.urgat.domain.export.DefaultExportUseCase.Companion.TODOS_COLUMNS_MAPPING
import com.ivzb.urgat.domain.export.DefaultExportUseCase.Companion.WEIGHTS_COLUMNS_MAPPING
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class DefaultExportUseCaseTest {

    private lateinit var exportRepository: ExportRepository
    private lateinit var todoRepository: TodoRepository
    private lateinit var notesRepository: NotesRepository
    private lateinit var expensesRepository: ExpensesRepository
    private lateinit var investmentsRepository: InvestmentsRepository
    private lateinit var weightsRepository: WeightsRepository
    private val dispatcher = StandardTestDispatcher()

    private lateinit var exportUseCase: ExportUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        exportRepository = mockk(relaxed = true)
        todoRepository = mockk(relaxed = true)
        notesRepository = mockk(relaxed = true)
        expensesRepository = mockk(relaxed = true)
        investmentsRepository = mockk(relaxed = true)
        weightsRepository = mockk(relaxed = true)

        exportUseCase = DefaultExportUseCase(
            exportRepository,
            todoRepository,
            notesRepository,
            expensesRepository,
            investmentsRepository,
            weightsRepository,
            dispatcher,
        )
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val uri = mockk<Uri>()

        val todosCursor = mockk<Cursor>()
        every { todoRepository.stream() } returns todosCursor

        val notesCursor = mockk<Cursor>()
        every { notesRepository.stream() } returns notesCursor

        val expensesCursor = mockk<Cursor>()
        every { expensesRepository.stream() } returns expensesCursor

        val investmentsCursor = mockk<Cursor>()
        every { investmentsRepository.stream() } returns investmentsCursor

        val weightsCursor = mockk<Cursor>()
        every { weightsRepository.stream() } returns weightsCursor

        exportUseCase(uri)

        coVerify(exactly = 1) {
            exportRepository.exportCsv(eq(uri), eq(todosCursor), eq("todos.csv"), eq(TODOS_COLUMNS_MAPPING))
            exportRepository.exportCsv(eq(uri), eq(notesCursor), eq("notes.csv"), eq(NOTES_COLUMNS_MAPPING))
            exportRepository.exportCsv(eq(uri), eq(expensesCursor), eq("expenses.csv"), eq(EXPENSES_COLUMNS_MAPPING))
            exportRepository.exportCsv(eq(uri), eq(investmentsCursor), eq("investments.csv"), eq(INVESTMENTS_COLUMNS_MAPPING))
            exportRepository.exportCsv(eq(uri), eq(weightsCursor), eq("weights.csv"), eq(WEIGHTS_COLUMNS_MAPPING))
        }
    }
}
