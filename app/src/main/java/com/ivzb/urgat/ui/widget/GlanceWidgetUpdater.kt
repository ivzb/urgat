package com.ivzb.urgat.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import com.ivzb.urgat.domain.widget.WidgetType
import com.ivzb.urgat.domain.widget.WidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * The one place the refresh logic touches Glance, kept as thin as it can be because none of it
 * can be covered by a JVM test.
 */
class GlanceWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetUpdater {

    override suspend fun placed(): Set<WidgetType> {
        val manager = GlanceAppWidgetManager(context)

        return WidgetType.entries
            .filter { type -> manager.getGlanceIds(type.widgetClass).isNotEmpty() }
            .toSet()
    }

    override suspend fun updateAll(type: WidgetType) {
        type.widget().updateAll(context)
    }

    private val WidgetType.widgetClass: Class<out GlanceAppWidget>
        get() = when (this) {
            WidgetType.NET_WORTH -> NetWorthWidget::class.java
            WidgetType.WATCHLIST -> WatchlistWidget::class.java
        }

    private fun WidgetType.widget(): GlanceAppWidget = when (this) {
        WidgetType.NET_WORTH -> NetWorthWidget()
        WidgetType.WATCHLIST -> WatchlistWidget()
    }

}

