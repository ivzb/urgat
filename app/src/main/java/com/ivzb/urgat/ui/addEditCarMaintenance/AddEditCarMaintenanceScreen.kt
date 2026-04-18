package com.ivzb.urgat.ui.addEditCarMaintenance

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

enum class CarMaintenanceFieldId {
    Description,
    PartNumber,
    Url,
    CarName,
    Odometer,
    Date,
}

@Composable
fun AddEditCarMaintenanceScreen(
    viewModel: AddEditCarMaintenanceViewModel,

    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditCarMaintenanceScreen(
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
fun AddEditCarMaintenanceScreen(
    uiState: AddEditCarMaintenanceUiState,
    onIntent: (AddEditCarMaintenanceIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequesters = remember {
        List(CarMaintenanceFieldId.entries.size) { FocusRequester() }
    }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = uiState.screenTitleResId,
                onBack = onBack,
                actionsContent = {
                    if (uiState.withDelete) {
                        DeleteButton(onClick = { onIntent(AddEditCarMaintenanceIntent.DeleteCarMaintenance) })
                    }

                    MoreMenu(
                        listOfNotNull(
                            (R.string.duplicate to { onIntent(AddEditCarMaintenanceIntent.DuplicateCarMaintenance) }).takeIf { uiState.withDuplicate },
                            (R.string.clear_defaults to { onIntent(AddEditCarMaintenanceIntent.ClearDefaults) }).takeIf { uiState.withClearDefaults },
                        )
                    )
                },
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

                TextField(
                    value = uiState.description,
                    onValueChange = {
                        onIntent(AddEditCarMaintenanceIntent.UpdateDescription(it))
                    },
                    placeholder = { Text(stringResource(id = R.string.car_maintenance_description_hint)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesters[CarMaintenanceFieldId.Description.ordinal]),
                )

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
                ) {
                    TextField(
                        value = uiState.partNumber,
                        onValueChange = {
                            onIntent(AddEditCarMaintenanceIntent.UpdatePartNumber(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.car_maintenance_part_number_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[CarMaintenanceFieldId.PartNumber.ordinal]),
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
                ) {

                    TextField(
                        value = uiState.url,
                        onValueChange = {
                            onIntent(AddEditCarMaintenanceIntent.UpdateUrl(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.car_maintenance_url_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Next,
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[CarMaintenanceFieldId.Url.ordinal]),
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.horizontal_margin))
                ) {
                    TextField(
                        value = uiState.carName,
                        onValueChange = {
                            onIntent(AddEditCarMaintenanceIntent.UpdateCarName(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.car_maintenance_car_name_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[CarMaintenanceFieldId.CarName.ordinal]),
                    )

                    TextField(
                        value = uiState.odometer,
                        onValueChange = {
                            onIntent(AddEditCarMaintenanceIntent.UpdateOdometer(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.car_maintenance_odometer_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[CarMaintenanceFieldId.Odometer.ordinal]),
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                DatePicker(
                    timestamp = uiState.date,
                    onTimestampSelected = {
                        onIntent(AddEditCarMaintenanceIntent.UpdateDate(it))
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
                            .focusRequester(focusRequesters[CarMaintenanceFieldId.Date.ordinal])
                            .onFocusChanged {
                                if (it.isFocused) {
                                    dialog.show()
                                    focusManager.clearFocus()
                                }
                            },
                    )
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )

                Button(
                    onClick = rememberDebouncedClick {
                        onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)
                    },
                    modifier = Modifier
                        .responsiveButtonWidth()
                ) {
                    Text(stringResource(R.string.save))
                }

                LaunchedEffect(uiState.focusIndex) {
                    focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
                }

                LaunchedEffect(uiState.isLoading) {
                    focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
                }
            }
        }
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)

        LaunchedEffect(userMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(snackbarText)
                onIntent(AddEditCarMaintenanceIntent.UserMessageShown)
            }
        }
    }
}

@AllDevicesPreview
@Composable
private fun AddCarMaintenanceScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditCarMaintenanceScreen(
                uiState = AddEditCarMaintenanceUiState(
                    screenTitleResId = R.string.add_car_maintenance,
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
private fun EditCarMaintenanceScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditCarMaintenanceScreen(
                uiState = AddEditCarMaintenanceUiState(
                    screenTitleResId = R.string.edit_car_maintenance,
                    description = "Oil change",
                    partNumber = "ABC123",
                    url = "https://example.com",
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

