package com.ivzb.urgat.di

import com.ivzb.urgat.domain.saving.DefaultObserveGroupedSavingCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.saving.DefaultObserveGroupedSavingCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.saving.DefaultObserveGroupedSavingCategoriesYearlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingCategoriesAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingCategoriesYearlyUseCase
import com.ivzb.urgat.domain.saving.CreateSavingUseCase
import com.ivzb.urgat.domain.saving.DefaultCreateSavingUseCase
import com.ivzb.urgat.domain.saving.DefaultDeleteSavingUseCase
import com.ivzb.urgat.domain.saving.DefaultGetSavingUseCase
import com.ivzb.urgat.domain.saving.DefaultObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.domain.saving.DefaultObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.saving.DefaultObserveGroupedSavingsYearlyUseCase
import com.ivzb.urgat.domain.saving.DefaultObserveSavingsSortingUseCase
import com.ivzb.urgat.domain.saving.DefaultObserveSavingsUseCase
import com.ivzb.urgat.domain.saving.DefaultUpdateSavingUseCase
import com.ivzb.urgat.domain.saving.DeleteSavingUseCase
import com.ivzb.urgat.domain.saving.GetSavingUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsYearlyUseCase
import com.ivzb.urgat.domain.saving.ObserveSavingsSortingUseCase
import com.ivzb.urgat.domain.saving.ObserveSavingsUseCase
import com.ivzb.urgat.domain.saving.UpdateSavingUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SavingsUseCaseModule {

    @Binds
    fun bindsGetSavingUseCase(useCase: DefaultGetSavingUseCase): GetSavingUseCase

    @Singleton
    @Binds
    fun bindsObserveSavingsUseCase(useCase: DefaultObserveSavingsUseCase): ObserveSavingsUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedSavingsMonthlyUseCase(useCase: DefaultObserveGroupedSavingsMonthlyUseCase): ObserveGroupedSavingsMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedSavingsYearlyUseCase(useCase: DefaultObserveGroupedSavingsYearlyUseCase): ObserveGroupedSavingsYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedSavingsAllTimeUseCase(useCase: DefaultObserveGroupedSavingsAllTimeUseCase): ObserveGroupedSavingsAllTimeUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedSavingCategoriesMonthlyUseCase(useCase: DefaultObserveGroupedSavingCategoriesMonthlyUseCase): ObserveGroupedSavingCategoriesMonthlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedSavingCategoriesYearlyUseCase(useCase: DefaultObserveGroupedSavingCategoriesYearlyUseCase): ObserveGroupedSavingCategoriesYearlyUseCase

    @Singleton
    @Binds
    fun bindsObserveGroupedSavingCategoriesAllTimeUseCase(useCase: DefaultObserveGroupedSavingCategoriesAllTimeUseCase): ObserveGroupedSavingCategoriesAllTimeUseCase

    @Singleton
    @Binds
    fun bindsUpdateSavingUseCase(useCase: DefaultUpdateSavingUseCase): UpdateSavingUseCase

    @Singleton
    @Binds
    fun bindsCreateSavingUseCase(useCase: DefaultCreateSavingUseCase): CreateSavingUseCase

    @Singleton
    @Binds
    fun bindsObserveSavingsSortingUseCase(useCase: DefaultObserveSavingsSortingUseCase): ObserveSavingsSortingUseCase

    @Singleton
    @Binds
    fun bindsDeleteSavingUseCase(useCase: DefaultDeleteSavingUseCase): DeleteSavingUseCase

}
