package com.ivzb.urgat.di

import com.ivzb.urgat.domain.export.DefaultExportUseCase
import com.ivzb.urgat.domain.export.ExportUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ExportUseCaseModule {

    @Singleton
    @Binds
    fun bindsExportUseCase(useCase: DefaultExportUseCase): ExportUseCase

}
