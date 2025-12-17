package com.ivzb.urgat.di

import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultCreateExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultDeleteExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultGetExpenseUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveGroupedExpensesUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.DefaultObserveExpensesUseCase
import com.ivzb.urgat.domain.expense.DefaultUpdateExpenseUseCase
import com.ivzb.urgat.domain.expense.DeleteExpenseUseCase
import com.ivzb.urgat.domain.expense.GetExpenseUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesUseCase
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
    fun bindsObserveGroupedExpensesUseCase(useCase: DefaultObserveGroupedExpensesUseCase): ObserveGroupedExpensesUseCase

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
    fun bindsDeleteExpenseUseCase(useCase: DefaultDeleteExpenseUseCase): DeleteExpenseUseCase

}
