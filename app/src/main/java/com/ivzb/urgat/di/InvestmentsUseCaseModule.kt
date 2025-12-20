package com.ivzb.urgat.di

import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultCreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultDeleteInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultGetInvestmentUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveGroupedInvestmentsUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentAssetsUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentCategoriesUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentNetWorthUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.DefaultObserveInvestmentsUseCase
import com.ivzb.urgat.domain.investment.DefaultUpdateInvestmentUseCase
import com.ivzb.urgat.domain.investment.DeleteInvestmentUseCase
import com.ivzb.urgat.domain.investment.GetInvestmentUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentAssetsUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentCategoriesUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentNetWorthUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsFilteringUseCase
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
    fun bindsObserveGroupedInvestmentsUseCase(useCase: DefaultObserveGroupedInvestmentsUseCase): ObserveGroupedInvestmentsUseCase

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
    fun bindsDeleteInvestmentUseCase(useCase: DefaultDeleteInvestmentUseCase): DeleteInvestmentUseCase

}
