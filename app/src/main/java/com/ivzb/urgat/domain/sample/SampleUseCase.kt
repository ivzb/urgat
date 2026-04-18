package com.ivzb.urgat.domain.sample

import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.domain.budget.CreateBudgetUseCase
import com.ivzb.urgat.domain.carMaintenance.CreateCarMaintenanceUseCase
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.fuelConsumption.CreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.saving.CreateSavingUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import com.ivzb.urgat.domain.watchlist.CreateWatchlistAssetUseCase
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
    private val createSavingUseCase: CreateSavingUseCase,
    private val createBudgetUseCase: CreateBudgetUseCase,
    private val createWatchlistAssetUseCase: CreateWatchlistAssetUseCase,
    private val createWeightUseCase: CreateWeightUseCase,
    private val createFuelConsumptionUseCase: CreateFuelConsumptionUseCase,
    private val createCarMaintenanceUseCase: CreateCarMaintenanceUseCase,
): SampleUseCase {

    override suspend operator fun invoke() {
        if (!BuildConfig.DEBUG) {
            return
        }

        SampleData.SAMPLE_TODOS.forEach {
            createTodoUseCase(
                it.content,
            )
        }

        SampleData.SAMPLE_NOTES.forEach {
            createNoteUseCase(
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

        SampleData.SAMPLE_WEIGHTS.forEach {
            createWeightUseCase(
                it.value,
                it.date,
            )
        }

        SampleData.SAMPLE_WATCHLIST_ASSETS.forEach {
            createWatchlistAssetUseCase(
                it.name,
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

        SampleData.SAMPLE_CAR_MAINTENANCE.forEach {
            createCarMaintenanceUseCase(
                it.description,
                it.partNumber,
                it.url,
                it.carName,
                it.odometer,
                it.date,
            )
        }
    }

}
