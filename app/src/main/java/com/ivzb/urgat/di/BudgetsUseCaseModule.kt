package com.ivzb.urgat.di

import com.ivzb.urgat.domain.budget.CreateBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultCreateBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultDeleteBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultGetBudgetUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveGroupedBudgetCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveGroupedBudgetCategoriesYearlyUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveGroupedBudgetsAllTimeUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveGroupedBudgetsMonthlyUseCase
import com.ivzb.urgat.domain.budget.DefaultObserveGroupedBudgetsYearlyUseCase
import com.ivzb.urgat.domain.budget.DefaultUpdateBudgetUseCase
import com.ivzb.urgat.domain.budget.DeleteBudgetUseCase
import com.ivzb.urgat.domain.budget.GetBudgetUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesYearlyUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetsAllTimeUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetsMonthlyUseCase
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetsYearlyUseCase
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

    @Binds
    fun bindsObserveBudgetUseCase(useCase: DefaultObserveBudgetsUseCase): ObserveBudgetsUseCase

    @Singleton
    @Binds
    fun bindsObserveBudgetsFilteringUseCase(useCase: DefaultObserveBudgetsFilteringUseCase): ObserveBudgetsFilteringUseCase

    @Singleton
    @Binds
    fun bindsObserveBudgetsSortingUseCase(useCase: DefaultObserveBudgetsSortingUseCase): ObserveBudgetsSortingUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedBudgetCategoriesMonthlyUseCase(useCase: DefaultObserveGroupedBudgetCategoriesMonthlyUseCase): ObserveGroupedBudgetCategoriesMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedBudgetCategoriesYearlyUseCase(useCase: DefaultObserveGroupedBudgetCategoriesYearlyUseCase): ObserveGroupedBudgetCategoriesYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedBudgetsCategoriesAllTimeUseCase(useCase: DefaultObserveGroupedBudgetCategoriesAllTimeUseCase): ObserveGroupedBudgetCategoriesAllTimeUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedBudgetsMonthlyUseCase(useCase: DefaultObserveGroupedBudgetsMonthlyUseCase): ObserveGroupedBudgetsMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedBudgetsYearlyUseCase(useCase: DefaultObserveGroupedBudgetsYearlyUseCase): ObserveGroupedBudgetsYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedBudgetsAllTimeUseCase(useCase: DefaultObserveGroupedBudgetsAllTimeUseCase): ObserveGroupedBudgetsAllTimeUseCase

}
