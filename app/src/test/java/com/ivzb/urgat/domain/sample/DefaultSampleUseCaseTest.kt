package com.ivzb.urgat.domain.sample

import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.budget.CreateBudgetUseCase
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.fuelConsumption.CreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.saving.CreateSavingUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import com.ivzb.urgat.domain.watchlist.CreateWatchlistAssetUseCase
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
    private lateinit var createSavingUseCase: CreateSavingUseCase
    private lateinit var createBudgetUseCase: CreateBudgetUseCase
    private lateinit var createWatchlistAssetsUseCase: CreateWatchlistAssetUseCase
    private lateinit var createWeightUseCase: CreateWeightUseCase
    private lateinit var createFuelConsumptionUseCase: CreateFuelConsumptionUseCase

    private lateinit var sampleUseCase: SampleUseCase

    @Before
    fun setUp() {
        createTodoUseCase = mockk(relaxed = true)
        createNoteUseCase = mockk(relaxed = true)
        createExpenseUseCase = mockk(relaxed = true)
        createInvestmentUseCase = mockk(relaxed = true)
        createSavingUseCase = mockk(relaxed = true)
        createBudgetUseCase = mockk(relaxed = true)
        createWatchlistAssetsUseCase = mockk(relaxed = true)
        createWeightUseCase = mockk(relaxed = true)
        createFuelConsumptionUseCase = mockk(relaxed = true)

        sampleUseCase = DefaultSampleUseCase(
            createTodoUseCase,
            createNoteUseCase,
            createExpenseUseCase,
            createInvestmentUseCase,
            createSavingUseCase,
            createBudgetUseCase,
            createWatchlistAssetsUseCase,
            createWeightUseCase,
            createFuelConsumptionUseCase,
        )
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        sampleUseCase()

        coVerify(exactly = 1) {
            SampleData.SAMPLE_TODOS.forEach {
                createTodoUseCase(
                    it.content,
                )
            }

            SampleData.SAMPLE_EXPENSES.forEach {
                createExpenseUseCase(
                    it.name,
                    it.amount,
                    it.currency,
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
                    it.costPrice,
                    it.category,
                    it.currency,
                    it.date,
                )
            }

            SampleData.SAMPLE_SAVINGS.forEach {
                createSavingUseCase(
                    it.name,
                    it.amount,
                    it.currency,
                    it.date,
                )
            }

            SampleData.SAMPLE_BUDGETS.forEach {
                createBudgetUseCase(
                    it.income,
                    it.bankStart,
                    it.bankEnd,
                    it.currency,
                    it.date,
                )
            }

            SampleData.SAMPLE_WATCHLIST_ASSETS.forEach {
                createWatchlistAssetsUseCase(
                    it.name,
                )
            }

            SampleData.SAMPLE_WEIGHTS.forEach {
                createWeightUseCase(
                    it.value,
                    it.date
                )
            }

            SampleData.SAMPLE_FUEL_CONSUMPTION.forEach {
                createFuelConsumptionUseCase(
                    it.amount,
                    it.price,
                    it.currency,
                    it.type,
                    it.carName,
                    it.odometer,
                    it.date,
                )
            }
        }

    }
}
