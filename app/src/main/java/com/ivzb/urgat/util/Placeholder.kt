package com.ivzb.urgat.util

/**
 * Shown wherever a number could not be resolved - an unfetched quote, a missing exchange rate.
 *
 * An em dash rather than a `0` or a blank, so "we do not know" can never be read as
 * "it is worth nothing".
 */
const val UNKNOWN = "—"

/**
 * Prefixes a number which is known to be incomplete.
 *
 * Some of the values behind it could not be resolved, so it is a floor rather than the truth.
 */
const val APPROXIMATE = "≈"

