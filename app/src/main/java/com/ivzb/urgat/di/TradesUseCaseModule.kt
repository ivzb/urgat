package com.ivzb.urgat.di

import com.ivzb.urgat.domain.trade.CreateTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultCreateTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultDeleteTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultGetLastUsedTradeCurrencyUseCase
import com.ivzb.urgat.domain.trade.DefaultGetLastUsedTradeNameUseCase
import com.ivzb.urgat.domain.trade.DefaultGetTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultUpdateTradeUseCase
import com.ivzb.urgat.domain.trade.DeleteTradeUseCase
import com.ivzb.urgat.domain.trade.GetLastUsedTradeCurrencyUseCase
import com.ivzb.urgat.domain.trade.GetLastUsedTradeNameUseCase
import com.ivzb.urgat.domain.trade.GetTradeUseCase
import com.ivzb.urgat.domain.trade.UpdateTradeUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveGroupedTradeCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveGroupedTradeCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveGroupedTradeCategoriesYearlyUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveGroupedTradesAllTimeUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveGroupedTradesMonthlyUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveGroupedTradesYearlyUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveTradesFilteringUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveTradesSortingUseCase
import com.ivzb.urgat.domain.trades.DefaultObserveTradesUiStateUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradeCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradeCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradeCategoriesYearlyUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesAllTimeUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesMonthlyUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesYearlyUseCase
import com.ivzb.urgat.domain.trades.ObserveTradesFilteringUseCase
import com.ivzb.urgat.domain.trades.ObserveTradesSortingUseCase
import com.ivzb.urgat.domain.trades.ObserveTradesUiStateUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TradesUseCaseModule {

    @Binds
    fun bindsGetTradeUseCase(useCase: DefaultGetTradeUseCase): GetTradeUseCase

    @Binds
    fun bindsGetLastUsedTradeNameUseCase(useCase: DefaultGetLastUsedTradeNameUseCase): GetLastUsedTradeNameUseCase

    @Binds
    fun bindsGetLastUsedTradeCurrencyUseCase(useCase: DefaultGetLastUsedTradeCurrencyUseCase): GetLastUsedTradeCurrencyUseCase

    @Singleton
    @Binds
    fun bindsCreateTradeUseCase(useCase: DefaultCreateTradeUseCase): CreateTradeUseCase

    @Singleton
    @Binds
    fun bindsUpdateTradeUseCase(useCase: DefaultUpdateTradeUseCase): UpdateTradeUseCase

    @Singleton
    @Binds
    fun bindsDeleteTradeUseCase(useCase: DefaultDeleteTradeUseCase): DeleteTradeUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedTradesMonthlyUseCase(useCase: DefaultObserveGroupedTradesMonthlyUseCase): ObserveGroupedTradesMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedTradesAllTimeUseCase(useCase: DefaultObserveGroupedTradesAllTimeUseCase): ObserveGroupedTradesAllTimeUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedTradesYearlyUseCase(useCase: DefaultObserveGroupedTradesYearlyUseCase): ObserveGroupedTradesYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedTradeCategoriesMonthlyUseCase(useCase: DefaultObserveGroupedTradeCategoriesMonthlyUseCase): ObserveGroupedTradeCategoriesMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedTradeCategoriesYearlyUseCase(useCase: DefaultObserveGroupedTradeCategoriesYearlyUseCase): ObserveGroupedTradeCategoriesYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedTradeCategoriesAllTimeUseCase(useCase: DefaultObserveGroupedTradeCategoriesAllTimeUseCase): ObserveGroupedTradeCategoriesAllTimeUseCase

    @Singleton
    @Binds
    fun bindsObserveTradesFilteringUseCase(useCase: DefaultObserveTradesFilteringUseCase): ObserveTradesFilteringUseCase

    @Singleton
    @Binds
    fun bindsObserveTradesSortingUseCase(useCase: DefaultObserveTradesSortingUseCase): ObserveTradesSortingUseCase

    @Singleton
    @Binds
    fun bindsObserveTradesUiStateUseCase(useCase: DefaultObserveTradesUiStateUseCase): ObserveTradesUiStateUseCase

}

