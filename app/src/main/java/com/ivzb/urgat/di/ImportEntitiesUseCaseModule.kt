package com.ivzb.urgat.di

import com.ivzb.urgat.domain.importData.DefaultImportBudgetsUseCase
import com.ivzb.urgat.domain.importData.DefaultImportCarMaintenanceUseCase
import com.ivzb.urgat.domain.importData.DefaultImportExpensesUseCase
import com.ivzb.urgat.domain.importData.DefaultImportFuelConsumptionUseCase
import com.ivzb.urgat.domain.importData.DefaultImportInvestmentsUseCase
import com.ivzb.urgat.domain.importData.DefaultImportNotesUseCase
import com.ivzb.urgat.domain.importData.DefaultImportSavingsUseCase
import com.ivzb.urgat.domain.importData.DefaultImportTodosUseCase
import com.ivzb.urgat.domain.importData.DefaultImportTradesUseCase
import com.ivzb.urgat.domain.importData.DefaultImportWatchlistAssetsUseCase
import com.ivzb.urgat.domain.importData.DefaultImportWeightsUseCase
import com.ivzb.urgat.domain.importData.ImportBudgetsUseCase
import com.ivzb.urgat.domain.importData.ImportCarMaintenanceUseCase
import com.ivzb.urgat.domain.importData.ImportExpensesUseCase
import com.ivzb.urgat.domain.importData.ImportFuelConsumptionUseCase
import com.ivzb.urgat.domain.importData.ImportInvestmentsUseCase
import com.ivzb.urgat.domain.importData.ImportNotesUseCase
import com.ivzb.urgat.domain.importData.ImportSavingsUseCase
import com.ivzb.urgat.domain.importData.ImportTodosUseCase
import com.ivzb.urgat.domain.importData.ImportTradesUseCase
import com.ivzb.urgat.domain.importData.ImportWatchlistAssetsUseCase
import com.ivzb.urgat.domain.importData.ImportWeightsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ImportEntitiesUseCaseModule {

    @Singleton
    @Binds
    fun bindsImportTodosUseCase(useCase: DefaultImportTodosUseCase): ImportTodosUseCase

    @Singleton
    @Binds
    fun bindsImportNotesUseCase(useCase: DefaultImportNotesUseCase): ImportNotesUseCase

    @Singleton
    @Binds
    fun bindsImportExpensesUseCase(useCase: DefaultImportExpensesUseCase): ImportExpensesUseCase

    @Singleton
    @Binds
    fun bindsImportInvestmentsUseCase(useCase: DefaultImportInvestmentsUseCase): ImportInvestmentsUseCase

    @Singleton
    @Binds
    fun bindsImportSavingsUseCase(useCase: DefaultImportSavingsUseCase): ImportSavingsUseCase

    @Singleton
    @Binds
    fun bindsImportBudgetsUseCase(useCase: DefaultImportBudgetsUseCase): ImportBudgetsUseCase

    @Singleton
    @Binds
    fun bindsImportWatchlistAssetsUseCase(useCase: DefaultImportWatchlistAssetsUseCase): ImportWatchlistAssetsUseCase

    @Singleton
    @Binds
    fun bindsImportWeightsUseCase(useCase: DefaultImportWeightsUseCase): ImportWeightsUseCase

    @Singleton
    @Binds
    fun bindsImportFuelConsumptionUseCase(useCase: DefaultImportFuelConsumptionUseCase): ImportFuelConsumptionUseCase

    @Singleton
    @Binds
    fun bindsImportCarMaintenanceUseCase(useCase: DefaultImportCarMaintenanceUseCase): ImportCarMaintenanceUseCase

    @Singleton
    @Binds
    fun bindsImportTradesUseCase(useCase: DefaultImportTradesUseCase): ImportTradesUseCase

}
