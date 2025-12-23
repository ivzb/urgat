package com.ivzb.urgat.di

import com.ivzb.urgat.domain.sample.DefaultSampleUseCase
import com.ivzb.urgat.domain.sample.SampleUseCase
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
