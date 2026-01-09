package com.ivzb.urgat.domain.sample

import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import com.ivzb.urgat.domain.weight.CreateWeightUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultSampleUseCaseTest {

    private lateinit var createTodoUseCase: CreateTodoUseCase
    private lateinit var createNoteUseCase: CreateNoteUseCase
    private lateinit var createExpenseUseCase: CreateExpenseUseCase
    private lateinit var createInvestmentUseCase: CreateInvestmentUseCase
    private lateinit var createWeightUseCase: CreateWeightUseCase

    private lateinit var sampleUseCase: SampleUseCase

    @Before
    fun setUp() {
        createTodoUseCase = mockk(relaxed = true)
        createNoteUseCase = mockk(relaxed = true)
        createExpenseUseCase = mockk(relaxed = true)
        createInvestmentUseCase = mockk(relaxed = true)
        createWeightUseCase = mockk(relaxed = true)

        sampleUseCase = DefaultSampleUseCase(
            createTodoUseCase,
            createNoteUseCase,
            createExpenseUseCase,
            createInvestmentUseCase,
            createWeightUseCase,
        )
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        sampleUseCase()

        coVerify(exactly = 1) {
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
}
