package com.ivzb.urgat.ui.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Registered in the manifest, this is what the launcher talks to when hosting [WatchlistWidget].
 */
class WatchlistWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WatchlistWidget()

}

