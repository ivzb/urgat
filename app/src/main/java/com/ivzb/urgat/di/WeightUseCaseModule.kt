package com.ivzb.urgat.di

import com.ivzb.urgat.domain.weight.CreateWeightUseCase
import com.ivzb.urgat.domain.weight.DefaultCreateWeightUseCase
import com.ivzb.urgat.domain.weight.DefaultDeleteWeightUseCase
import com.ivzb.urgat.domain.weight.DefaultGetWeightUseCase
import com.ivzb.urgat.domain.weight.DefaultObserveWeightsSortingUseCase
import com.ivzb.urgat.domain.weight.DefaultObserveWeightsUiStateUseCase
import com.ivzb.urgat.domain.weight.DefaultObserveWeightsUseCase
import com.ivzb.urgat.domain.weight.DefaultUpdateWeightUseCase
import com.ivzb.urgat.domain.weight.DeleteWeightUseCase
import com.ivzb.urgat.domain.weight.GetWeightUseCase
import com.ivzb.urgat.domain.weight.ObserveWeightsSortingUseCase
import com.ivzb.urgat.domain.weight.ObserveWeightsUiStateUseCase
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import com.ivzb.urgat.domain.weight.UpdateWeightUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface WeightUseCaseModule {

    @Singleton
    @Binds
    fun bindsObserveWeightsUseCase(useCase: DefaultObserveWeightsUseCase): ObserveWeightsUseCase

    @Singleton
    @Binds
    fun bindsGetWeightUseCase(useCase: DefaultGetWeightUseCase): GetWeightUseCase

    @Singleton
    @Binds
    fun bindsCreateWeightUseCase(useCase: DefaultCreateWeightUseCase): CreateWeightUseCase

    @Singleton
    @Binds
    fun bindsUpdateWeightUseCase(useCase: DefaultUpdateWeightUseCase): UpdateWeightUseCase

    @Singleton
    @Binds
    fun bindsDeleteWeightUseCase(useCase: DefaultDeleteWeightUseCase): DeleteWeightUseCase

    @Singleton
    @Binds
    fun bindsObserveWeightsSortingUseCase(useCase: DefaultObserveWeightsSortingUseCase): ObserveWeightsSortingUseCase

    @Singleton
    @Binds
    fun bindsObserveWeightsUiStateUseCase(useCase: DefaultObserveWeightsUiStateUseCase): ObserveWeightsUiStateUseCase

}
