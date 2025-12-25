package com.ivzb.urgat.domain.export

import android.net.Uri
import androidx.annotation.VisibleForTesting
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.export.ExportMapping
import com.ivzb.urgat.data.export.ExportRepository
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ExportUseCase {

    suspend operator fun invoke(uri: Uri)

}

class DefaultExportUseCase @Inject constructor(
    private val exportRepository: ExportRepository,
    private val todoRepository: TodoRepository,
    private val notesRepository: NotesRepository,
    private val expensesRepository: ExpensesRepository,
    private val investmentsRepository: InvestmentsRepository,
    private val weightsRepository: WeightsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): ExportUseCase {

    override suspend operator fun invoke(uri: Uri) {
        return withContext(dispatcher) {
            exportRepository.exportCsv(uri, todoRepository.stream(), TODOS_FILE_NAME, TODOS_COLUMNS_MAPPING)
            exportRepository.exportCsv(uri, notesRepository.stream(), NOTES_FILE_NAME, NOTES_COLUMNS_MAPPING)
            exportRepository.exportCsv(uri, expensesRepository.stream(), EXPENSES_FILE_NAME, EXPENSES_COLUMNS_MAPPING)
            exportRepository.exportCsv(uri, investmentsRepository.stream(), INVESTMENTS_FILE_NAME, INVESTMENTS_COLUMNS_MAPPING)
            exportRepository.exportCsv(uri, weightsRepository.stream(), WEIGHTS_FILE_NAME, WEIGHTS_COLUMNS_MAPPING)
        }
    }

    companion object {

        private const val TODOS_FILE_NAME = "todos.csv"
        private const val NOTES_FILE_NAME = "notes.csv"
        private const val EXPENSES_FILE_NAME = "expenses.csv"
        private const val INVESTMENTS_FILE_NAME = "investments.csv"
        private const val WEIGHTS_FILE_NAME = "weights.csv"

        @VisibleForTesting
        val TODOS_COLUMNS_MAPPING = listOf(
            ExportMapping("id", "string"),
            ExportMapping("title", "string"),
            ExportMapping("description", "string"),
            ExportMapping("isCompleted", "boolean"),
            ExportMapping("createdAt", "long"),
        )

        @VisibleForTesting
        val NOTES_COLUMNS_MAPPING = listOf(
            ExportMapping("id", "string"),
            ExportMapping("content", "string"),
            ExportMapping("createdAt", "long"),
        )

        @VisibleForTesting
        val EXPENSES_COLUMNS_MAPPING = listOf(
            ExportMapping("id", "string"),
            ExportMapping("name", "string"),
            ExportMapping("amount", "big_decimal"),
            ExportMapping("category", "string"),
            ExportMapping("date", "long"),
            ExportMapping("createdAt", "long"),
        )

        @VisibleForTesting
        val INVESTMENTS_COLUMNS_MAPPING = listOf(
            ExportMapping("id", "string"),
            ExportMapping("name", "string"),
            ExportMapping("shares", "big_decimal"),
            ExportMapping("cost", "big_decimal"),
            ExportMapping("category", "string"),
            ExportMapping("currency", "string"),
            ExportMapping("date", "long"),
            ExportMapping("createdAt", "long"),
        )

        @VisibleForTesting
        val WEIGHTS_COLUMNS_MAPPING = listOf(
            ExportMapping("id", "string"),
            ExportMapping("value", "big_decimal"),
            ExportMapping("date", "long"),
            ExportMapping("createdAt", "long"),
        )

    }
}
