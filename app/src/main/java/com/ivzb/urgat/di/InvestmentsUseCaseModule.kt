package com.ivzb.urgat.di

import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultCreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultDeleteInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultGetInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveGroupedInvestmentCategoriesYearlyUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveGroupedInvestmentsYearlyUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentAssetsUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentCategoriesUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentNetWorthUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentsSortingUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentsUseCase
import com.ivzb.urgat.domain.investment.DefaultUpdateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DeleteInvestmentUseCase
import com.ivzb.urgat.domain.investment.GetInvestmentUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentCategoriesYearlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsYearlyUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentAssetsUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentCategoriesUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentNetWorthUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsSortingUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase
import com.ivzb.urgat.domain.investment.UpdateInvestmentUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface InvestmentsUseCaseModule {

    @Binds
    fun bindsGetInvestmentUseCase(useCase: DefaultGetInvestmentUseCase): GetInvestmentUseCase

    @Singleton
    @Binds
    fun bindsObserveInvestmentsUseCase(useCase: DefaultObserveInvestmentsUseCase): ObserveInvestmentsUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedInvestmentsMonthlyUseCase(useCase: DefaultObserveGroupedInvestmentsMonthlyUseCase): ObserveGroupedInvestmentsMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedInvestmentsYearlyUseCase(useCase: DefaultObserveGroupedInvestmentsYearlyUseCase): ObserveGroupedInvestmentsYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedInvestmentCategoriesYearlyUseCase(useCase: DefaultObserveGroupedInvestmentCategoriesYearlyUseCase): ObserveGroupedInvestmentCategoriesYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveInvestmentAssetsUseCase(useCase: DefaultObserveInvestmentAssetsUseCase): ObserveInvestmentAssetsUseCase

    @Singleton
    @Binds
    fun bindsObserveInvestmentCategoriesUseCase(useCase: DefaultObserveInvestmentCategoriesUseCase): ObserveInvestmentCategoriesUseCase

    @Singleton
    @Binds
    fun bindsObserveInvestmentNetWorthUseCase(useCase: DefaultObserveInvestmentNetWorthUseCase): ObserveInvestmentNetWorthUseCase

    @Singleton
    @Binds
    fun bindsUpdateInvestmentUseCase(useCase: DefaultUpdateInvestmentUseCase): UpdateInvestmentUseCase

    @Singleton
    @Binds
    fun bindsCreateInvestmentUseCase(useCase: DefaultCreateInvestmentUseCase): CreateInvestmentUseCase

    @Singleton
    @Binds
    fun bindsObserveInvestmentsFilteringUseCase(useCase: DefaultObserveInvestmentsFilteringUseCase): ObserveInvestmentsFilteringUseCase

    @Singleton
    @Binds
    fun bindsObserveInvestmentsSortingUseCase(useCase: DefaultObserveInvestmentsSortingUseCase): ObserveInvestmentsSortingUseCase

    @Singleton
    @Binds
    fun bindsDeleteInvestmentUseCase(useCase: DefaultDeleteInvestmentUseCase): DeleteInvestmentUseCase

}
