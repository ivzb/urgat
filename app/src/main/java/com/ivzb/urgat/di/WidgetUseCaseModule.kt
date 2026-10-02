package com.ivzb.urgat.di

import com.ivzb.urgat.domain.widget.DefaultRefreshQuotesUseCase
import com.ivzb.urgat.domain.widget.DefaultRefreshWidgetPricesUseCase
import com.ivzb.urgat.domain.widget.RefreshQuotesUseCase
import com.ivzb.urgat.domain.widget.RefreshWidgetPricesUseCase
import com.ivzb.urgat.domain.widget.WidgetUpdater
import com.ivzb.urgat.ui.widget.GlanceWidgetUpdater

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface WidgetUseCaseModule {

    @Binds
    fun bindsWidgetUpdater(updater: GlanceWidgetUpdater): WidgetUpdater

    @Binds
    fun bindsRefreshQuotesUseCase(useCase: DefaultRefreshQuotesUseCase): RefreshQuotesUseCase

    @Binds
    fun bindsRefreshWidgetPricesUseCase(useCase: DefaultRefreshWidgetPricesUseCase): RefreshWidgetPricesUseCase

}


