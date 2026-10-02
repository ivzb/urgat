package com.ivzb.urgat.domain.widget

class FakeWidgetUpdater : WidgetUpdater {

    /** Which widgets the home screen should pretend to have. */
    val placedWidgets = mutableSetOf<WidgetType>()

    /** Every widget that was told to rebuild, in the order it was told. */
    val updated = mutableListOf<WidgetType>()

    override suspend fun placed(): Set<WidgetType> = placedWidgets.toSet()

    override suspend fun updateAll(type: WidgetType) {
        updated += type
    }

}


