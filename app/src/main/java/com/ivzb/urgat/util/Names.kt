package com.ivzb.urgat.util

import android.content.Context
import com.ivzb.urgat.R

/**
 * Lists names in one line, cut short once there are more than a reader has room for.
 *
 * A home screen widget is a few centimetres wide, so a portfolio which lost every quote would
 * otherwise push a wall of symbols through a two line message and name none of them legibly.
 * Naming the first few and counting the rest is what keeps the line readable while still
 * pointing at something concrete.
 */
fun Context.formatNames(names: List<String>, limit: Int): String =
    formatNames(names, limit) { shown, remaining ->
        getString(R.string.names_and_more, shown, remaining)
    }

/**
 * The wording is the only part which needs a [Context], so the deciding is kept out of one.
 *
 * @param andMore how to word the names which did not fit, given the ones which did.
 */
fun formatNames(
    names: List<String>,
    limit: Int,
    andMore: (shown: String, remaining: Int) -> String,
): String {
    if (names.isEmpty()) {
        return ""
    }

    if (names.size <= limit) {
        return names.joinToString(NAME_SEPARATOR)
    }

    // a limit of zero would name nothing at all and count everything, which says no more than
    // the generic message it is meant to improve on
    val shown = names.take(limit.coerceAtLeast(1))

    return andMore(shown.joinToString(NAME_SEPARATOR), names.size - shown.size)
}

private const val NAME_SEPARATOR = ", "


