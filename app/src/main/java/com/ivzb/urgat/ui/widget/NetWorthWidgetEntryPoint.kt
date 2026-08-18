package com.ivzb.urgat.ui.widget

import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.domain.fire.ObserveNetWorthUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * [GlanceAppWidget][androidx.glance.appwidget.GlanceAppWidget] and
 * [ActionCallback][androidx.glance.appwidget.action.ActionCallback] are instantiated by the
 * framework, so they cannot be injected directly. This entry point gives them access to the
 * singleton graph instead.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetWorthWidgetEntryPoint {

    fun observeNetWorthUseCase(): ObserveNetWorthUseCase

    fun forexRepository(): ForexRepository

    fun marketPriceRepository(): MarketPriceRepository

}

