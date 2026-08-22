package com.ivzb.urgat.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Puts the widget into a loading state, drops the cached market prices, then waits for the
 * watchlist to be emitted again with fresh quotes before showing the content again.
 */
class RefreshWatchlistAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val entryPoint = context.watchlistWidgetEntryPoint()

        setRefreshing(context, glanceId, isRefreshing = true)

        try {
            entryPoint.marketPriceRepository().clearCache()

            withTimeoutOrNull(WatchlistWidget.REFRESH_TIMEOUT_MILLIS) {
                // nothing is emitted until every price has been fetched again
                entryPoint.observeWatchlistAssets().first()
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
            preferences[WatchlistWidget.REFRESH_STARTED_AT] = if (isRefreshing) {
                System.currentTimeMillis()
            } else {
                0L
            }
        }

        WatchlistWidget().update(context, glanceId)
    }

}

