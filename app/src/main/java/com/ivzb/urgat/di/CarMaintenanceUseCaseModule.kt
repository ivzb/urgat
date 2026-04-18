package com.ivzb.urgat.di

import com.ivzb.urgat.domain.carMaintenance.CreateCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultCreateCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultDeleteCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultGetCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultGetLastUsedCarMaintenanceCarNameUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultObserveCarMaintenanceFilteringUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultObserveCarMaintenanceSortingUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultObserveCarMaintenanceUiStateUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultObserveCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultObserveGroupedCarMaintenanceMonthlyUseCase
import com.ivzb.urgat.domain.carMaintenance.DefaultUpdateCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.DeleteCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.GetCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.GetLastUsedCarMaintenanceCarNameUseCase
import com.ivzb.urgat.domain.carMaintenance.ObserveCarMaintenanceFilteringUseCase
import com.ivzb.urgat.domain.carMaintenance.ObserveCarMaintenanceSortingUseCase
import com.ivzb.urgat.domain.carMaintenance.ObserveCarMaintenanceUiStateUseCase
import com.ivzb.urgat.domain.carMaintenance.ObserveCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.ObserveGroupedCarMaintenanceMonthlyUseCase
import com.ivzb.urgat.domain.carMaintenance.UpdateCarMaintenanceUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CarMaintenanceUseCaseModule {

    @Singleton
    @Binds
    fun bindsCreateCarMaintenanceUseCase(useCase: DefaultCreateCarMaintenanceUseCase): CreateCarMaintenanceUseCase

    @Singleton
    @Binds
    fun bindsGetCarMaintenanceUseCase(useCase: DefaultGetCarMaintenanceUseCase): GetCarMaintenanceUseCase

    @Singleton
    @Binds
    fun bindsUpdateCarMaintenanceUseCase(useCase: DefaultUpdateCarMaintenanceUseCase): UpdateCarMaintenanceUseCase

    @Singleton
    @Binds
    fun bindsDeleteCarMaintenanceUseCase(useCase: DefaultDeleteCarMaintenanceUseCase): DeleteCarMaintenanceUseCase

    @Singleton
    @Binds
    fun bindsGetLastUsedCarMaintenanceCarNameUseCase(useCase: DefaultGetLastUsedCarMaintenanceCarNameUseCase): GetLastUsedCarMaintenanceCarNameUseCase

    @Singleton
    @Binds
    fun bindsObserveCarMaintenanceUseCase(useCase: DefaultObserveCarMaintenanceUseCase): ObserveCarMaintenanceUseCase

    @Singleton
    @Binds
    fun bindsObserveCarMaintenanceFilteringUseCase(useCase: DefaultObserveCarMaintenanceFilteringUseCase): ObserveCarMaintenanceFilteringUseCase

    @Singleton
    @Binds
    fun bindsObserveCarMaintenanceSortingUseCase(useCase: DefaultObserveCarMaintenanceSortingUseCase): ObserveCarMaintenanceSortingUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedCarMaintenanceMonthlyUseCase(useCase: DefaultObserveGroupedCarMaintenanceMonthlyUseCase): ObserveGroupedCarMaintenanceMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveCarMaintenanceUiStateUseCase(useCase: DefaultObserveCarMaintenanceUiStateUseCase): ObserveCarMaintenanceUiStateUseCase

}

