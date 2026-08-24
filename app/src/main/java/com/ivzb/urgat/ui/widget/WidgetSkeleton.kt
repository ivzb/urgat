package com.ivzb.urgat.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.height
import androidx.glance.layout.width
import com.ivzb.urgat.R

/**
 * Placeholder standing in for a line of content which is still being loaded.
 *
 * Widgets are drawn with `RemoteViews`, so it cannot shimmer - it is sized after the content it
 * replaces instead, to keep the layout from jumping once the data is in.
 */
@Composable
fun SkeletonBar(width: Dp, height: Dp) {
    Box(
        modifier = GlanceModifier
            .width(width)
            .height(height)
            .background(ImageProvider(R.drawable.widget_skeleton)),
        content = {},
    )
}

