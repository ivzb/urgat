package com.ivzb.urgat.ui.finance

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.util.format

/**
 * Explains how FiRe works, along with the numbers [fire] was calculated from.
 */
@Composable
fun fireInfoText(fire: FiRe): String = StringBuilder().apply {
    appendLine(stringResource(R.string.fire_info))
    appendLine()

    append(fireStashText(fire))

    appendLine()

    appendLine(
        stringResource(
            R.string.fire_annual_income,
            "${fire.annualIncome.format()} ${fire.currency}",
        )
    )
    appendLine(
        stringResource(
            R.string.fire_annual_expenses,
            "${fire.annualExpenses.format()} ${fire.currency}",
        )
    )
    appendLine(
        stringResource(
            R.string.fire_annual_investments,
            "${fire.annualInvestments.format()} ${fire.currency}",
        )
    )

    appendLine()

    appendLine(
        stringResource(
            R.string.fire_savings_rate,
            fire.savingsRate?.format() ?: EMPTY_FIRE_CALCULATION
        )
    )
    appendLine(
        stringResource(
            R.string.fire_target,
            fire.target?.let { target -> "${target.format()} ${fire.currency}" } ?: EMPTY_FIRE_CALCULATION
        )
    )

    appendLine()

    appendLine(
        stringResource(
            R.string.fire_years_until_fire,
            fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION
        )
    )
}.toString()

/**
 * The accumulated stash broken down per asset, along with whatever could not be valued.
 */
@Composable
private fun fireStashText(fire: FiRe): String = StringBuilder().apply {
    appendLine(
        stringResource(
            R.string.fire_accumulated_stash,
            "${fire.accumulatedStash.format()} ${fire.currency}",
        )
    )

    fire.stashBreakdown.forEach { item ->
        appendLine("    ${item.name}: ${item.amount.format()} ${fire.currency}")
    }

    if (fire.isPartial) {
        appendLine()
        appendLine(stringResource(R.string.fire_stash_partial))

        // a dialog has room to spell every culprit out, unlike the widget - so it
        // names them all rather than counting the ones which did not fit
        if (fire.unavailableAssets.isNotEmpty()) {
            appendLine(
                stringResource(
                    R.string.fire_stash_unavailable_assets,
                    fire.unavailableAssets.joinToString(", "),
                )
            )
        }

        if (fire.unavailableRates.isNotEmpty()) {
            appendLine(
                stringResource(
                    R.string.fire_stash_unavailable_rates,
                    fire.unavailableRates.joinToString(", "),
                )
            )
        }
    }
}.toString()
