package com.ivzb.urgat.di

import com.ivzb.urgat.domain.importData.DefaultImportDataUseCase
import com.ivzb.urgat.domain.importData.ImportDataUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ImportDataUseCaseModule {

    @Singleton
    @Binds
    fun bindsImportDataUseCase(useCase: DefaultImportDataUseCase): ImportDataUseCase

}
