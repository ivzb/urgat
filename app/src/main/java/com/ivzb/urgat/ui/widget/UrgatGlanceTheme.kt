package com.ivzb.urgat.ui.widget

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.ivzb.urgat.ui.theme.darkScheme
import com.ivzb.urgat.ui.theme.lightScheme

private val UrgatGlanceColors = ColorProviders(
    light = lightScheme,
    dark = darkScheme,
)

/**
 * [GlanceTheme] backed by the very same color schemes the app itself uses.
 */
@Composable
fun UrgatGlanceTheme(content: @Composable () -> Unit) {
    GlanceTheme(
        colors = UrgatGlanceColors,
        content = content,
    )
}

