package com.ivzb.urgat.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Puts the widget into a loading state, drops the cached forex and market prices, then waits for
 * the net worth to be recalculated with fresh quotes before showing the content again.
 */
class RefreshNetWorthAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val entryPoint = context.netWorthWidgetEntryPoint()

        setRefreshing(context, glanceId, isRefreshing = true)

        try {
            entryPoint.forexRepository().clearCache()
            entryPoint.marketPriceRepository().clearCache()

            withTimeoutOrNull(NetWorthWidget.REFRESH_TIMEOUT_MILLIS) {
                entryPoint.observeNetWorthUseCase().invoke().first { it !is Async.Loading }
            }
        } finally {
            // the content has to come back even when the receiver is being torn down
            withContext(NonCancellable) {
                setRefreshing(context, glanceId, isRefreshing = false)
            }
        }
    }

    private suspend fun setRefreshing(context: Context, glanceId: GlanceId, isRefreshing: Boolean) {
        updateAppWidgetState(context, glanceId) { preferences ->
            preferences[NetWorthWidget.REFRESH_STARTED_AT] = if (isRefreshing) {
                System.currentTimeMillis()
            } else {
                0L
            }
        }

        NetWorthWidget().update(context, glanceId)
    }

}

