package com.ivzb.urgat.ui.finance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.ivzb.urgat.R
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.ui.components.ScreenTitleWithInfo
import com.ivzb.urgat.ui.fire.FireDateRangeType
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate

internal const val EMPTY_FIRE_CALCULATION = "∞"

@Composable
internal fun FiRePage(
    fire: FiRe?,
    fireDateRangeType: FireDateRangeType,
    fireCustomStart: Long,
    fireCustomEnd: Long,
    onDateRangeTypeChange: (FireDateRangeType) -> Unit,
    onCustomDateRangeChange: (Long, Long) -> Unit,
    onWithdrawalRateChange: (String) -> Unit,
    onReturnOfInvestmentChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                vertical = dimensionResource(R.dimen.content_padding_vertical),
            )
    ) {

        FireDateRangeSection(
            fireDateRangeType = fireDateRangeType,
            fireCustomStart = fireCustomStart,
            fireCustomEnd = fireCustomEnd,
            onDateRangeTypeChange = onDateRangeTypeChange,
            onCustomDateRangeChange = onCustomDateRangeChange,
        )

        if (fire == null) {
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

            Text(
                text = stringResource(R.string.fire_not_enough_data),
                style = MaterialTheme.typography.bodyLarge,
            )
            return
        }

        FireCalculationSection(
            fire = fire,
            onWithdrawalRateChange = onWithdrawalRateChange,
            onReturnOfInvestmentChange = onReturnOfInvestmentChange,
            onTaxRateChange = onTaxRateChange,
        )
    }
}

@Composable
private fun FireDateRangeSection(
    fireDateRangeType: FireDateRangeType,
    fireCustomStart: Long,
    fireCustomEnd: Long,
    onDateRangeTypeChange: (FireDateRangeType) -> Unit,
    onCustomDateRangeChange: (Long, Long) -> Unit,
) {
    Text(
        text = stringResource(R.string.fire_date_range),
    )

    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        FireDateRangeType.entries.forEachIndexed { index, type ->
            SegmentedButton(
                selected = fireDateRangeType == type,
                onClick = { onDateRangeTypeChange(type) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = FireDateRangeType.entries.size,
                ),
                icon = {},
                border = SegmentedButtonDefaults.borderStroke(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                ),
            ) {
                Text(stringResource(type.labelResId))
            }
        }
    }

    if (fireDateRangeType == FireDateRangeType.CUSTOM) {
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

        FireCustomDateRangePicker(
            customStart = fireCustomStart,
            customEnd = fireCustomEnd,
            onCustomDateRangeChange = onCustomDateRangeChange,
        )
    }
}

@Composable
private fun FireCalculationSection(
    fire: FiRe,
    onWithdrawalRateChange: (String) -> Unit,
    onReturnOfInvestmentChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,
) {
    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

    ScreenTitleWithInfo(
        title = stringResource(R.string.fire_title, fire.selection),
        dialogTitle = stringResource(R.string.how_does_it_work),
        dialogText = fireInfoText(fire),
    )

    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

    FireRateLabelsRow()

    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.fire_margin)))

    FireRateInputsRow(
        fire = fire,
        onWithdrawalRateChange = onWithdrawalRateChange,
        onReturnOfInvestmentChange = onReturnOfInvestmentChange,
        onTaxRateChange = onTaxRateChange,
    )

    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.vertical_margin)))

    Text(
        text = stringResource(
            R.string.fire_years_until_fire,
            fire.yearsUntilFiRe?.format() ?: EMPTY_FIRE_CALCULATION
        ),
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun FireRateLabelsRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.fire_withdrawal_rate),
        )

        Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.fire_return_of_investment),
        )

        Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.fire_tax_rate),
        )
    }
}

@Composable
private fun FireRateInputsRow(
    fire: FiRe,
    onWithdrawalRateChange: (String) -> Unit,
    onReturnOfInvestmentChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        var withdrawalRateValue by remember {
            mutableStateOf(
                TextFieldValue(
                    fire.withdrawalRate?.toPlainString() ?: ""
                )
            )
        }
        var returnOfInvestmentValue by remember {
            mutableStateOf(
                TextFieldValue(
                    fire.returnOfInvestment?.toPlainString() ?: ""
                )
            )
        }
        var taxRateValue by remember {
            mutableStateOf(
                TextFieldValue(
                    fire.taxRate?.toPlainString() ?: ""
                )
            )
        }

        WithdrawalRateTextField(withdrawalRateValue = withdrawalRateValue, fire = fire, onWithdrawalRateChange = onWithdrawalRateChange)

        Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

        ReturnOfInvestmentTextField(returnOfInvestmentValue = returnOfInvestmentValue, fire = fire, onReturnOfInvestmentChange = onReturnOfInvestmentChange)

        Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

        TaxRateTextField(taxRateValue = taxRateValue, fire = fire, onTaxRateChange = onTaxRateChange)

        LaunchedEffect(fire) {
            val withdrawalRate = fire.withdrawalRate?.toPlainString() ?: ""
            val returnOfInvestment = fire.returnOfInvestment?.toPlainString() ?: ""
            val taxRate = fire.taxRate?.toPlainString() ?: ""

            withdrawalRateValue = TextFieldValue(
                text = withdrawalRate,
                selection = TextRange(withdrawalRate.length)
            )

            returnOfInvestmentValue = TextFieldValue(
                text = returnOfInvestment,
                selection = TextRange(returnOfInvestment.length)
            )

            taxRateValue = TextFieldValue(
                text = taxRate,
                selection = TextRange(taxRate.length)
            )
        }
    }
}

@Composable
private fun RowScope.WithdrawalRateTextField(
    withdrawalRateValue: TextFieldValue,
    fire: FiRe,
    onWithdrawalRateChange: (String) -> Unit,
) {
    TextField(
        modifier = Modifier.weight(1f),
        value = withdrawalRateValue,
        onValueChange = { onWithdrawalRateChange(it.text) },
        placeholder = {
            Text(
                stringResource(
                    R.string.format_default,
                    fire.withdrawalRatePlaceholder.toPlainString()
                )
            )
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
    )
}

@Composable
private fun RowScope.ReturnOfInvestmentTextField(
    returnOfInvestmentValue: TextFieldValue,
    fire: FiRe,
    onReturnOfInvestmentChange: (String) -> Unit,
) {
    TextField(
        modifier = Modifier.weight(1f),
        value = returnOfInvestmentValue,
        onValueChange = { onReturnOfInvestmentChange(it.text) },
        placeholder = {
            Text(
                stringResource(
                    R.string.format_default,
                    fire.returnOfInvestmentPlaceholder.toPlainString()
                )
            )
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
    )
}

@Composable
private fun RowScope.TaxRateTextField(
    taxRateValue: TextFieldValue,
    fire: FiRe,
    onTaxRateChange: (String) -> Unit,
) {
    TextField(
        modifier = Modifier.weight(1f),
        value = taxRateValue,
        onValueChange = { onTaxRateChange(it.text) },
        placeholder = {
            Text(
                stringResource(
                    R.string.format_default,
                    fire.taxRatePlaceholder.toPlainString()
                )
            )
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FireCustomDateRangePicker(
    customStart: Long,
    customEnd: Long,
    onCustomDateRangeChange: (Long, Long) -> Unit,
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = { showStartPicker = true },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = if (customStart > 0L) {
                    customStart.toLocalDate().format()
                } else {
                    stringResource(R.string.fire_select_start_date)
                }
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.fire_margin)))

        OutlinedButton(
            onClick = { showEndPicker = true },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = if (customEnd > 0L) {
                    customEnd.toLocalDate().format()
                } else {
                    stringResource(R.string.fire_select_end_date)
                }
            )
        }
    }

    if (showStartPicker) {
        FireDatePickerDialog(
            title = stringResource(R.string.fire_custom_start_date),
            initialSelectedDateMillis = customStart.takeIf { it > 0L },
            onDateSelected = { selectedDate ->
                selectedDate?.let { start ->
                    onCustomDateRangeChange(start, if (customEnd > 0L) customEnd else start)
                }
            },
            onDismiss = { showStartPicker = false },
        )
    }

    if (showEndPicker) {
        FireDatePickerDialog(
            title = stringResource(R.string.fire_custom_end_date),
            initialSelectedDateMillis = customEnd.takeIf { it > 0L },
            onDateSelected = { selectedDate ->
                selectedDate?.let { end ->
                    onCustomDateRangeChange(if (customStart > 0L) customStart else end, end)
                }
            },
            onDismiss = { showEndPicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FireDatePickerDialog(
    title: String,
    initialSelectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
            title = { Text(text = title, modifier = Modifier.padding(start = dimensionResource(R.dimen.content_padding_horizontal), top = dimensionResource(R.dimen.content_padding_vertical))) },
        )
    }
}
