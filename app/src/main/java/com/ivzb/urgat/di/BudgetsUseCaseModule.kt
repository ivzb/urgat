package com.ivzb.urgat.di

import com.ivzb.urgat.domain.budget.CreateBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultCreateBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultDeleteBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultGetBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultUpdateBudgetUseCase
import com.ivzb.urgat.domain.budget.DeleteBudgetUseCase
import com.ivzb.urgat.domain.budget.GetBudgetUseCase
import com.ivzb.urgat.domain.budget.UpdateBudgetUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BudgetsUseCaseModule {

    @Binds
    fun bindsGetBudgetUseCase(useCase: DefaultGetBudgetUseCase): GetBudgetUseCase

    @Singleton
    @Binds
    fun bindsUpdateBudgetUseCase(useCase: DefaultUpdateBudgetUseCase): UpdateBudgetUseCase

    @Singleton
    @Binds
    fun bindsCreateBudgetUseCase(useCase: DefaultCreateBudgetUseCase): CreateBudgetUseCase

    @Singleton
    @Binds
    fun bindsDeleteBudgetUseCase(useCase: DefaultDeleteBudgetUseCase): DeleteBudgetUseCase

}
