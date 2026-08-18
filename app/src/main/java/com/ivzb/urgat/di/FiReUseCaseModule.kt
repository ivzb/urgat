package com.ivzb.urgat.di

import com.ivzb.urgat.domain.fire.DefaultObserveFiReUseCase
import com.ivzb.urgat.domain.fire.DefaultObserveNetWorthUseCase
import com.ivzb.urgat.domain.fire.ObserveFiReUseCase
import com.ivzb.urgat.domain.fire.ObserveNetWorthUseCase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FiReUseCaseModule {

    @Binds
    fun bindsObserveFiReUseCase(useCase: DefaultObserveFiReUseCase): ObserveFiReUseCase

    @Binds
    fun bindsObserveNetWorthUseCase(useCase: DefaultObserveNetWorthUseCase): ObserveNetWorthUseCase

}