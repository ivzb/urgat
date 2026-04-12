package com.ivzb.urgat.di

import com.ivzb.urgat.domain.fuelConsumption.CreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultCreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveFuelConsumptionSortingUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveGroupedFuelConsumptionMonthlyUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionSortingUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveGroupedFuelConsumptionMonthlyUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FuelConsumptionUseCaseModule {

    @Singleton
    @Binds
    fun bindsCreateFuelConsumptionUseCase(useCase: DefaultCreateFuelConsumptionUseCase): CreateFuelConsumptionUseCase

    @Singleton
    @Binds
    fun bindsObserveFuelConsumptionUseCase(useCase: DefaultObserveFuelConsumptionUseCase): ObserveFuelConsumptionUseCase

    @Singleton
    @Binds
    fun bindsObserveFuelConsumptionSortingUseCase(useCase: DefaultObserveFuelConsumptionSortingUseCase): ObserveFuelConsumptionSortingUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedFuelConsumptionMonthlyUseCase(useCase: DefaultObserveGroupedFuelConsumptionMonthlyUseCase): ObserveGroupedFuelConsumptionMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveFuelConsumptionUiStateUseCase(useCase: DefaultObserveFuelConsumptionUiStateUseCase): ObserveFuelConsumptionUiStateUseCase

}

