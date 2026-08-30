package com.ivzb.urgat.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.text.font.FontWeight as ComposeFontWeight

/**
 * Draws an [AnnotatedString] the app already builds - a price with its P&L next to it, say - inside
 * a widget.
 *
 * Glance has no annotated text of its own, every `Text` carries exactly one style, so each stretch
 * of the string becomes a `Text` of its own laid out in a row. The point is that the widget and the
 * screen say the same thing the same way instead of each formatting its own.
 *
 * Meant for single line strings - a row does not wrap, so a line break in [text] is not one.
 */
@Composable
fun AnnotatedText(
    text: AnnotatedString,
    style: TextStyle,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Vertical.CenterVertically,
    ) {

        text.runs().forEach { (run, spanStyle) ->
            Text(
                text = run,
                style = style.mergeWith(spanStyle),
                maxLines = 1,
            )
        }
    }
}

/**
 * Cuts the string into the plain and the styled stretches, in the order they are written.
 *
 * The spans the app builds never overlap, but a run is clamped to what is left anyway - a widget
 * silently dropping a stretch of an amount beats it crashing the launcher.
 */
private fun AnnotatedString.runs(): List<Pair<String, SpanStyle?>> {
    val runs = mutableListOf<Pair<String, SpanStyle?>>()
    var index = 0

    spanStyles.sortedBy { it.start }.forEach { range ->
        val start = range.start.coerceIn(index, text.length)
        val end = range.end.coerceIn(start, text.length)

        if (start > index) {
            runs += text.substring(index, start) to null
        }

        runs += text.substring(start, end) to range.item
        index = end
    }

    if (index < text.length) {
        runs += text.substring(index) to null
    }

    return runs.filterNot { (run, _) -> run.isEmpty() }
}

/** Whatever the span has an opinion about wins, the rest is left to the widget's own style. */
private fun TextStyle.mergeWith(spanStyle: SpanStyle?): TextStyle {
    if (spanStyle == null) {
        return this
    }

    return copy(
        color = spanStyle.color
            .takeUnless { it == Color.Unspecified }
            ?.let { ColorProvider(it) }
            ?: color,
        fontSize = spanStyle.fontSize.takeIf { it.isSp } ?: fontSize,
        fontWeight = spanStyle.fontWeight?.toGlanceFontWeight() ?: fontWeight,
    )
}

/** Glance only knows the three weights, so anything in between is rounded down to the nearest. */
private fun ComposeFontWeight.toGlanceFontWeight(): FontWeight = when {
    this >= ComposeFontWeight.Bold -> FontWeight.Bold
    this >= ComposeFontWeight.Medium -> FontWeight.Medium
    else -> FontWeight.Normal
}

