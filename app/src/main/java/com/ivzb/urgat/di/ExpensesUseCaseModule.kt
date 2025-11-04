package com.ivzb.urgat.di

import com.ivzb.urgat.data.expense.useCase.CreateExpenseUseCase
import com.ivzb.urgat.data.expense.useCase.DefaultCreateExpenseUseCase
import com.ivzb.urgat.data.expense.useCase.DefaultGetExpenseUseCase
import com.ivzb.urgat.data.expense.useCase.DefaultObserveExpensesUseCase
import com.ivzb.urgat.data.expense.useCase.DefaultUpdateExpenseUseCase
import com.ivzb.urgat.data.expense.useCase.GetExpenseUseCase
import com.ivzb.urgat.data.expense.useCase.ObserveExpensesUseCase
import com.ivzb.urgat.data.expense.useCase.UpdateExpenseUseCase
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
    fun bindsUpdateExpenseUseCase(useCase: DefaultUpdateExpenseUseCase): UpdateExpenseUseCase

    @Singleton
    @Binds
    fun bindsCreateExpenseUseCase(useCase: DefaultCreateExpenseUseCase): CreateExpenseUseCase

}
