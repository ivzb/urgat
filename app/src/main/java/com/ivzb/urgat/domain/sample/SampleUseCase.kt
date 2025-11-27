package com.ivzb.urgat.domain.sample

import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import javax.inject.Inject

interface SampleUseCase {

    suspend operator fun invoke()

}

class DefaultSampleUseCase @Inject constructor(
    private val createTodoUseCase: CreateTodoUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val createInvestmentUseCase: CreateInvestmentUseCase,
): SampleUseCase {

    override suspend operator fun invoke() {
        if (!BuildConfig.DEBUG) {
            return
        }

        SampleData.SAMPLE_TODOS.forEach { (title, description) ->
            createTodoUseCase(title, description)
        }

        SampleData.SAMPLE_EXPENSES.forEach { (name, amount, category) ->
            createExpenseUseCase(name, amount.toBigDecimal(), category, System.currentTimeMillis())
        }

        SampleData.SAMPLE_NOTES.forEach { (content) ->
            createNoteUseCase(content)
        }

        SampleData.SAMPLE_INVESTMENTS.forEach {
            createInvestmentUseCase(
                it.name,
                it.amount,
                it.cost,
                it.category,
                it.currency,
                System.currentTimeMillis()
            )
        }
    }

}
