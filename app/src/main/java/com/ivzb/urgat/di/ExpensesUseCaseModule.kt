package com.ivzb.urgat.di

import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultCreateExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultDeleteExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultGetExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveExpensesSortingUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveExpensesUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveGroupedExpenseCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveGroupedExpenseCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveGroupedExpenseCategoriesYearlyUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveGroupedExpensesYearlyUseCase
import com.ivzb.urgat.domain.expense.DefaultUpdateExpenseUseCase
import com.ivzb.urgat.domain.expense.DeleteExpenseUseCase
import com.ivzb.urgat.domain.expense.GetExpenseUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesSortingUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpenseCategoriesYearlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesYearlyUseCase
import com.ivzb.urgat.domain.expense.UpdateExpenseUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ExpensesUseCaseModule {

    @Binds
    fun bindsGetExpenseUseCase(useCase: DefaultGetExpenseUseCase): GetExpenseUseCase

    @Singleton
    @Binds
    fun bindsObserveExpensesUseCase(useCase: DefaultObserveExpensesUseCase): ObserveExpensesUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedExpensesMonthlyUseCase(useCase: DefaultObserveGroupedExpensesMonthlyUseCase): ObserveGroupedExpensesMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedExpensesYearlyUseCase(useCase: DefaultObserveGroupedExpensesYearlyUseCase): ObserveGroupedExpensesYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedExpenseCategoriesMonthlyUseCase(useCase: DefaultObserveGroupedExpenseCategoriesMonthlyUseCase): ObserveGroupedExpenseCategoriesMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedExpenseCategoriesYearlyUseCase(useCase: DefaultObserveGroupedExpenseCategoriesYearlyUseCase): ObserveGroupedExpenseCategoriesYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedExpenseCategoriesAllTimeUseCase(useCase: DefaultObserveGroupedExpenseCategoriesAllTimeUseCase): ObserveGroupedExpenseCategoriesAllTimeUseCase

    @Singleton
    @Binds
    fun bindsUpdateExpenseUseCase(useCase: DefaultUpdateExpenseUseCase): UpdateExpenseUseCase

    @Singleton
    @Binds
    fun bindsCreateExpenseUseCase(useCase: DefaultCreateExpenseUseCase): CreateExpenseUseCase

    @Singleton
    @Binds
    fun bindsObserveExpensesFilteringUseCase(useCase: DefaultObserveExpensesFilteringUseCase): ObserveExpensesFilteringUseCase

    @Singleton
    @Binds
    fun bindsObserveExpensesSortingUseCase(useCase: DefaultObserveExpensesSortingUseCase): ObserveExpensesSortingUseCase

    @Singleton
    @Binds
    fun bindsDeleteExpenseUseCase(useCase: DefaultDeleteExpenseUseCase): DeleteExpenseUseCase

}
