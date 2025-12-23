package com.ivzb.urgat.domain.export

import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.data.weight.WeightsRepository
import javax.inject.Inject

interface ExportUseCase {

    suspend operator fun invoke()

}

class DefaultExportUseCase @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notesRepository: NotesRepository,
    private val expensesRepository: ExpensesRepository,
    private val investmentsRepository: InvestmentsRepository,
    private val weightsRepository: WeightsRepository,
): ExportUseCase {

    override suspend operator fun invoke() {
        val streams = listOf(
            todoRepository.stream(),
            notesRepository.stream(),
            expensesRepository.stream(),
            investmentsRepository.stream(),
            weightsRepository.stream(),
        )

        // todo: export to csv
    }

}
