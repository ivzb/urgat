package com.ivzb.urgat.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

fun annotateFormattedString(
    formattedString: String,
    part: String,
    spanStyle: SpanStyle,
): AnnotatedString {
    return buildAnnotatedString {
        append(formattedString)

        val partStart = formattedString.indexOf(part)

        if (partStart >= 0) {
            addStyle(
                spanStyle,
                partStart,
                partStart + part.length
            )
        }
    }
}