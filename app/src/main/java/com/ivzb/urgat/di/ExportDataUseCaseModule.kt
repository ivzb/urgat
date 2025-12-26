package com.ivzb.urgat.di

import com.ivzb.urgat.domain.exportData.DefaultExportDataUseCase
import com.ivzb.urgat.domain.exportData.ExportDataUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ExportDataUseCaseModule {

    @Singleton
    @Binds
    fun bindsExportDataUseCase(useCase: DefaultExportDataUseCase): ExportDataUseCase

}
