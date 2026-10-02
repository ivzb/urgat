package com.ivzb.urgat

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ivzb.urgat.worker.RefreshWidgetPricesWorker
import com.ivzb.urgat.worker.WeightReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class UrgatApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleWeightReminder()
        scheduleWidgetPriceRefresh()
    }

    private fun scheduleWeightReminder() {
        val workRequest = PeriodicWorkRequestBuilder<WeightReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WeightReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
    }

    private fun scheduleWidgetPriceRefresh() {
        val workRequest = PeriodicWorkRequestBuilder<RefreshWidgetPricesWorker>(
            repeatInterval = RefreshWidgetPricesWorker.REPEAT_INTERVAL_MINUTES,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
        )
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RefreshWidgetPricesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }
}
