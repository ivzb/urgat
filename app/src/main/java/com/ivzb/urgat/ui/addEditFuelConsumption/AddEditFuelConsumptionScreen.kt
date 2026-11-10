package com.ivzb.urgat.ui.addEditFuelConsumption

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.components.DatePicker
import com.ivzb.urgat.ui.components.DeleteButton
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.MoreMenu
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.components.rememberDebouncedClick
import com.ivzb.urgat.ui.components.responsiveButtonWidth
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

enum class FieldId {
    Amount,
    Price,
    Currency,
    Type,
    CarName,
    Odometer,
    Date,
}

@Composable
fun AddEditFuelConsumptionScreen(
    viewModel: AddEditFuelConsumptionViewModel,

    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditFuelConsumptionScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier
    )

    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            onBack()
        }
    }
}

@VisibleForTesting
@Composable
fun AddEditFuelConsumptionScreen(
    uiState: AddEditFuelConsumptionUiState,
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequesters = remember {
        List(FieldId.entries.size) { FocusRequester() }
    }

    Scaffold(
        topBar = {
            AddEditFuelConsumptionTopAppBar(
                uiState = uiState,
                onIntent = onIntent,
                onBack = onBack,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(
                    horizontal = dimensionResource(R.dimen.content_padding_horizontal),
                    vertical = dimensionResource(R.dimen.content_padding_vertical),
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                LoadingState(
                    label = R.string.loading,
                    modifier = Modifier.weight(1f)
                )
            } else {
                AddEditFuelConsumptionForm(
                    uiState = uiState,
                    onIntent = onIntent,
                    focusRequesters = focusRequesters,
                )
            }
        }
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)

        LaunchedEffect(userMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(snackbarText)
                onIntent(AddEditFuelConsumptionIntent.UserMessageShown)
            }
        }
    }
}

@Composable
private fun AddEditFuelConsumptionTopAppBar(
    uiState: AddEditFuelConsumptionUiState,
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
    onBack: () -> Unit,
) {
    TextTopAppBar(
        title = uiState.screenTitleResId,
        onBack = onBack,
        actionsContent = {
            if (uiState.withDelete) {
                DeleteButton(onClick = { onIntent(AddEditFuelConsumptionIntent.DeleteFuelConsumption) })
            }

            MoreMenu(
                listOfNotNull(
                    (R.string.duplicate to { onIntent(AddEditFuelConsumptionIntent.DuplicateFuelConsumption) }).takeIf { uiState.withDuplicate },
                    (R.string.clear_defaults to { onIntent(AddEditFuelConsumptionIntent.ClearDefaults) }).takeIf { uiState.withClearDefaults },
                )
            )
        },
    )
}

@Composable
private fun ColumnScope.AddEditFuelConsumptionForm(
    uiState: AddEditFuelConsumptionUiState,
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    AmountAndPriceRow(
        uiState = uiState,
        onIntent = onIntent,
        focusRequesters = focusRequesters,
    )

    Spacer(
        modifier = Modifier
            .height(dimensionResource(R.dimen.vertical_margin))
    )

    CurrencyAndTypeRow(
        uiState = uiState,
        onIntent = onIntent,
        focusRequesters = focusRequesters,
    )

    Spacer(
        modifier = Modifier
            .height(dimensionResource(R.dimen.vertical_margin))
    )

    CarNameAndOdometerRow(
        uiState = uiState,
        onIntent = onIntent,
        focusRequesters = focusRequesters,
    )

    Spacer(
        modifier = Modifier
            .height(dimensionResource(R.dimen.vertical_margin))
    )

    DateRow(
        date = uiState.date,
        onIntent = onIntent,
        focusRequesters = focusRequesters,
    )

    Spacer(
        modifier = Modifier
            .weight(1f)
    )

    SaveButton(
        onIntent = onIntent,
    )

    LaunchedEffect(uiState.focusIndex) {
        focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
    }

    LaunchedEffect(uiState.isLoading) {
        focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
    }
}

@Composable
private fun AmountAndPriceRow(
    uiState: AddEditFuelConsumptionUiState,
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
    ) {
        TextField(
            value = uiState.amount,
            onValueChange = {
                onIntent(AddEditFuelConsumptionIntent.UpdateAmount(it))
            },
            placeholder = { Text(stringResource(id = R.string.fuel_consumption_amount_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequesters[FieldId.Amount.ordinal]),
        )

        TextField(
            value = uiState.price,
            onValueChange = {
                onIntent(AddEditFuelConsumptionIntent.UpdatePrice(it))
            },
            placeholder = { Text(stringResource(id = R.string.fuel_consumption_price_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequesters[FieldId.Price.ordinal]),
        )
    }
}

@Composable
private fun CurrencyAndTypeRow(
    uiState: AddEditFuelConsumptionUiState,
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
    ) {
        TextField(
            value = uiState.currency,
            onValueChange = {
                onIntent(AddEditFuelConsumptionIntent.UpdateCurrency(it))
            },
            placeholder = { Text(stringResource(id = R.string.currency_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequesters[FieldId.Currency.ordinal]),
        )

        TextField(
            value = uiState.type,
            onValueChange = {
                onIntent(AddEditFuelConsumptionIntent.UpdateType(it))
            },
            placeholder = { Text(stringResource(id = R.string.fuel_consumption_type_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequesters[FieldId.Type.ordinal]),
        )
    }
}

@Composable
private fun CarNameAndOdometerRow(
    uiState: AddEditFuelConsumptionUiState,
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
    ) {
        TextField(
            value = uiState.carName,
            onValueChange = {
                onIntent(AddEditFuelConsumptionIntent.UpdateCarName(it))
            },
            placeholder = { Text(stringResource(id = R.string.fuel_consumption_car_name_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequesters[FieldId.CarName.ordinal]),
        )

        TextField(
            value = uiState.odometer,
            onValueChange = {
                onIntent(AddEditFuelConsumptionIntent.UpdateOdometer(it))
            },
            placeholder = { Text(stringResource(id = R.string.fuel_consumption_odometer_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequesters[FieldId.Odometer.ordinal]),
        )
    }
}

@Composable
private fun DateRow(
    date: Long,
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
    focusRequesters: List<FocusRequester>,
) {
    val focusManager = LocalFocusManager.current

    DatePicker(
        timestamp = date,
        onTimestampSelected = {
            onIntent(AddEditFuelConsumptionIntent.UpdateDate(it))
        }
    ) { selectedDate, dialog ->

        TextField(
            value = selectedDate,
            onValueChange = {},
            placeholder = { Text(stringResource(id = R.string.date_hint)) },
            label = { Text(stringResource(id = R.string.date_hint)) },
            readOnly = true,
            maxLines = 1,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(id = R.string.date_hint)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesters[FieldId.Date.ordinal])
                .onFocusChanged {
                    if (it.isFocused) {
                        dialog.show()
                        focusManager.clearFocus()
                    }
                },
        )
    }
}

@Composable
private fun SaveButton(
    onIntent: (AddEditFuelConsumptionIntent) -> Unit,
) {
    Button(
        onClick = rememberDebouncedClick {
            onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)
        },
        modifier = Modifier
            .responsiveButtonWidth()
    ) {
        Text(stringResource(R.string.save))
    }
}

@AllDevicesPreview
@Composable
private fun AddFuelConsumptionScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditFuelConsumptionScreen(
                uiState = AddEditFuelConsumptionUiState(
                    screenTitleResId = R.string.add_fuel_consumption,
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}

@AllDevicesPreview
@Composable
private fun EditFuelConsumptionScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditFuelConsumptionScreen(
                uiState = AddEditFuelConsumptionUiState(
                    screenTitleResId = R.string.edit_fuel_consumption,
                    amount = "45.5",
                    price = "2.35",
                    currency = "EUR",
                    type = "Diesel",
                    carName = "Mercedes w140",
                    odometer = "150000",
                    date = 0L,
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}

@AllDevicesPreview
@Composable
private fun LoadingFuelConsumptionScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditFuelConsumptionScreen(
                uiState = AddEditFuelConsumptionUiState(
                    screenTitleResId = R.string.edit_fuel_consumption,
                    isLoading = true
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}
