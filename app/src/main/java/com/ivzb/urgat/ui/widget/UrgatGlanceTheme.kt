package com.ivzb.urgat.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
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

/**
 * The style every widget labels its content with.
 */
@Composable
fun widgetTitleStyle(): TextStyle = TextStyle(
    color = GlanceTheme.colors.onSurfaceVariant,
    fontSize = 13.sp,
    fontWeight = FontWeight.Medium,
)

