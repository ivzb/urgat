package com.ivzb.urgat.domain.widget

/** The home screen widgets whose prices are worth keeping fresh. */
enum class WidgetType {

    NET_WORTH,

    WATCHLIST,

}

/**
 * The home screen, as much of it as the refresh logic needs to know about.
 *
 * Exists so that logic stays a plain class that can be tested on the JVM - the real
 * implementation reaches for `AppWidgetManager` and Glance, neither of which runs without a
 * device or Robolectric.
 */
interface WidgetUpdater {

    /** Which widgets are actually on a home screen right now. */
    suspend fun placed(): Set<WidgetType>

    /** Rebuilds every instance of [type] from whatever the repositories now hold. */
    suspend fun updateAll(type: WidgetType)

}

