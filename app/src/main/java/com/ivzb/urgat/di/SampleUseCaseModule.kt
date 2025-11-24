package com.ivzb.urgat.di

import com.ivzb.urgat.domain.sample.DefaultSampleUseCase
import com.ivzb.urgat.domain.sample.SampleUseCase
import com.ivzb.urgat.domain.weight.CreateWeightUseCase
import com.ivzb.urgat.domain.weight.DefaultCreateWeightUseCase
import com.ivzb.urgat.domain.weight.DefaultObserveWeightsUseCase
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SampleUseCaseModule {

    @Singleton
    @Binds
    fun bindsSampleUseCase(useCase: DefaultSampleUseCase): SampleUseCase

}
