package com.ivzb.urgat.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import dagger.hilt.android.EntryPointAccessors

/**
 * Drops the cached forex and market prices so the next composition recalculates the net worth
 * with fresh quotes.
 */
class RefreshNetWorthAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            NetWorthWidgetEntryPoint::class.java,
        )

        entryPoint.forexRepository().clearCache()
        entryPoint.marketPriceRepository().clearCache()

        NetWorthWidget().update(context, glanceId)
    }

}

