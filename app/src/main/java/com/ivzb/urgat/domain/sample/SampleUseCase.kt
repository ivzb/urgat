package com.ivzb.urgat.domain.sample

import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import com.ivzb.urgat.domain.weight.CreateWeightUseCase
import javax.inject.Inject

interface SampleUseCase {

    suspend operator fun invoke()

}

class DefaultSampleUseCase @Inject constructor(
    private val createTodoUseCase: CreateTodoUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val createInvestmentUseCase: CreateInvestmentUseCase,
    private val createWeightUseCase: CreateWeightUseCase,
): SampleUseCase {

    override suspend operator fun invoke() {
        if (!BuildConfig.DEBUG) {
            return
        }

        SampleData.SAMPLE_TODOS.forEach {
            createTodoUseCase(
                it.title,
                it.description,
            )
        }

        SampleData.SAMPLE_EXPENSES.forEach {
            createExpenseUseCase(
                it.name,
                it.amount,
                it.category,
                it.date,
                it.excludeFromAnalytics,
            )
        }

        SampleData.SAMPLE_NOTES.forEach {
            createNoteUseCase(
                it.content,
            )
        }

        SampleData.SAMPLE_INVESTMENTS.forEach {
            createInvestmentUseCase(
                it.name,
                it.shares,
                it.cost,
                it.category,
                it.currency,
                it.date,
            )
        }

        SampleData.SAMPLE_WEIGHTS.forEach {
            createWeightUseCase(
                it.value,
                it.date
            )
        }
    }

}
