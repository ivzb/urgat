package com.ivzb.urgat.domain.widget

import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.util.Logger
import javax.inject.Inject

enum class RefreshWidgetPricesResult {

    REFRESHED,

    /** No widget is on a home screen, so no request was worth making. */
    NOTHING_TO_REFRESH,

    /** The quotes did not arrive in time. The widgets keep whatever they were showing. */
    TIMED_OUT,

}

/**
 * Refreshes every home screen widget which is actually placed, then rebuilds it.
 *
 * The widgets cannot do this for themselves. A price is only ever fetched while
 * [MarketPriceRepository] is being asked for one, and that only happens when an observed flow
 * emits - on first collection, on a database change, or on `refreshFlow`. The widgets'
 * `updatePeriodMillis` tick recomposes but never re-collects, so without something actively
 * pushing, a widget left alone redraws the same numbers until the process is killed and the
 * in-memory cache dies with it.
 *
 * This is the push. It is the same [RefreshQuotesUseCase] the refresh button runs, driven by a
 * timer instead of a finger.
 */
interface RefreshWidgetPricesUseCase {

    suspend operator fun invoke(): RefreshWidgetPricesResult

}

class DefaultRefreshWidgetPricesUseCase @Inject constructor(
    private val widgetUpdater: WidgetUpdater,
    private val refreshQuotes: RefreshQuotesUseCase,
    private val logger: Logger,
) : RefreshWidgetPricesUseCase {

    override suspend fun invoke(): RefreshWidgetPricesResult {
        val placed = widgetUpdater.placed()

        // nobody is looking, so there is nothing worth spending a request on
        if (placed.isEmpty()) {
            logger.debug("widget price refresh skipped, no widgets placed")
            return RefreshWidgetPricesResult.NOTHING_TO_REFRESH
        }

        // the quotes are awaited here rather than left to the widgets, so the network call lands
        // inside the window the caller guarantees - and so the widgets rebuild straight from a
        // warm cache instead of flashing a loading state at the user
        if (!refreshQuotes(placed, FETCH_TIMEOUT_MILLIS)) {
            logger.debug("widget price refresh timed out")
            return RefreshWidgetPricesResult.TIMED_OUT
        }

        // a widget with no live Glance session is not listening to the caches, so it has to be
        // told to rebuild
        placed.forEach { type -> widgetUpdater.updateAll(type) }

        logger.debug("widget price refresh done")

        return RefreshWidgetPricesResult.REFRESHED
    }

    companion object {

        /** Generous next to the widget refresh button - a background refresh is not watched. */
        internal const val FETCH_TIMEOUT_MILLIS = 60_000L

    }

}
