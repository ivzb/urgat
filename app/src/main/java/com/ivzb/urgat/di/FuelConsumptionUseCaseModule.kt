package com.ivzb.urgat.di

import com.ivzb.urgat.domain.fuelConsumption.CreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultCreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultDeleteFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultGetFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultGetLastUsedFuelConsumptionCarNameUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultGetLastUsedFuelConsumptionCurrencyUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultGetLastUsedFuelConsumptionTypeUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveFuelConsumptionSortingUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultObserveGroupedFuelConsumptionMonthlyUseCase
import com.ivzb.urgat.domain.fuelConsumption.DefaultUpdateFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.DeleteFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetLastUsedFuelConsumptionCarNameUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetLastUsedFuelConsumptionCurrencyUseCase
import com.ivzb.urgat.domain.fuelConsumption.GetLastUsedFuelConsumptionTypeUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionSortingUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveGroupedFuelConsumptionMonthlyUseCase
import com.ivzb.urgat.domain.fuelConsumption.UpdateFuelConsumptionUseCase
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
    fun bindsGetFuelConsumptionUseCase(useCase: DefaultGetFuelConsumptionUseCase): GetFuelConsumptionUseCase

    @Singleton
    @Binds
    fun bindsUpdateFuelConsumptionUseCase(useCase: DefaultUpdateFuelConsumptionUseCase): UpdateFuelConsumptionUseCase

    @Singleton
    @Binds
    fun bindsDeleteFuelConsumptionUseCase(useCase: DefaultDeleteFuelConsumptionUseCase): DeleteFuelConsumptionUseCase

    @Singleton
    @Binds
    fun bindsGetLastUsedFuelConsumptionCurrencyUseCase(useCase: DefaultGetLastUsedFuelConsumptionCurrencyUseCase): GetLastUsedFuelConsumptionCurrencyUseCase

    @Singleton
    @Binds
    fun bindsGetLastUsedFuelConsumptionTypeUseCase(useCase: DefaultGetLastUsedFuelConsumptionTypeUseCase): GetLastUsedFuelConsumptionTypeUseCase

    @Singleton
    @Binds
    fun bindsGetLastUsedFuelConsumptionCarNameUseCase(useCase: DefaultGetLastUsedFuelConsumptionCarNameUseCase): GetLastUsedFuelConsumptionCarNameUseCase

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

