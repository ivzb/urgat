package com.ivzb.urgat.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ivzb.urgat.domain.widget.RefreshWidgetPricesResult
import com.ivzb.urgat.domain.widget.RefreshWidgetPricesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Puts [RefreshWidgetPricesUseCase] on a schedule.
 *
 * Deliberately holds no logic of its own - a `CoroutineWorker` cannot be built without a `Context`
 * and `WorkerParameters`, so anything living here would need Robolectric or `work-testing` to
 * cover. Everything worth asserting on sits in the use case instead.
 */
@HiltWorker
class RefreshWidgetPricesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshWidgetPrices: RefreshWidgetPricesUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = when (refreshWidgetPrices()) {
        // the widgets keep showing the last good values, so a retry is cheaper than a redraw
        RefreshWidgetPricesResult.TIMED_OUT -> Result.retry()

        RefreshWidgetPricesResult.REFRESHED,
        RefreshWidgetPricesResult.NOTHING_TO_REFRESH -> Result.success()
    }

    companion object {

        const val WORK_NAME = "refresh_widget_prices_work"

        /**
         * Matches the `updatePeriodMillis` the widgets are declared with.
         *
         * WorkManager will not go below 15 minutes, and Doze defers it regardless, so this is the
         * interval asked for rather than the interval delivered.
         */
        const val REPEAT_INTERVAL_MINUTES = 30L

    }

}
