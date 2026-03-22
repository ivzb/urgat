package com.ivzb.urgat.di

import com.ivzb.urgat.domain.finance.DefaultObserveFinanceUiStateUseCase
import com.ivzb.urgat.domain.finance.ObserveFinanceUiStateUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FinanceUseCaseModule {

    @Singleton
    @Binds
    fun bindsObserveFinanceUiStateUseCase(useCase: DefaultObserveFinanceUiStateUseCase): ObserveFinanceUiStateUseCase

}

