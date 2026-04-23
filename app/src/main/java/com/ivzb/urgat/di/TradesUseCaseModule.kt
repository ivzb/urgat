package com.ivzb.urgat.di

import com.ivzb.urgat.domain.trade.CreateTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultCreateTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultDeleteTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultGetLastUsedTradeCurrencyUseCase
import com.ivzb.urgat.domain.trade.DefaultGetTradeUseCase
import com.ivzb.urgat.domain.trade.DefaultUpdateTradeUseCase
import com.ivzb.urgat.domain.trade.DeleteTradeUseCase
import com.ivzb.urgat.domain.trade.GetLastUsedTradeCurrencyUseCase
import com.ivzb.urgat.domain.trade.GetTradeUseCase
import com.ivzb.urgat.domain.trade.UpdateTradeUseCase
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

}

