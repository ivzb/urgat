package com.ivzb.urgat.ui.widget

import android.content.Context
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.domain.fire.ObserveNetWorthUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Widgets are the one place the app reaches into the Hilt graph by hand instead of being injected.
 *
 * Glance instantiates [GlanceAppWidget][androidx.glance.appwidget.GlanceAppWidget] and
 * [ActionCallback][androidx.glance.appwidget.action.ActionCallback] itself - the latter by
 * reflection, which requires a no-arg constructor - so neither is an Android component Hilt can
 * inject into, and a widget has no `ViewModelStoreOwner` to hang a `@HiltViewModel` off either.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetWorthWidgetEntryPoint {

    fun observeNetWorthUseCase(): ObserveNetWorthUseCase

    fun forexRepository(): ForexRepository

    fun marketPriceRepository(): MarketPriceRepository

}

internal fun Context.netWorthWidgetEntryPoint(): NetWorthWidgetEntryPoint =
    EntryPointAccessors.fromApplication(
        applicationContext,
        NetWorthWidgetEntryPoint::class.java,
    )

