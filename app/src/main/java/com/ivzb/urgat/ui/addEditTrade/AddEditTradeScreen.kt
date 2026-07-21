package com.ivzb.urgat.ui.addEditTrade

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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

enum class TradeFieldId {
    Name,
    Amount,
    Currency,
    Date,
}

@Composable
fun AddEditTradeScreen(
    viewModel: AddEditTradeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddEditTradeScreen(
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
fun AddEditTradeScreen(
    uiState: AddEditTradeUiState,
    onIntent: (AddEditTradeIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusRequesters = remember {
        List(TradeFieldId.entries.size) { FocusRequester() }
    }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = uiState.screenTitleResId,
                onBack = onBack,
                actionsContent = {
                    if (uiState.withDelete) {
                        DeleteButton(onClick = { onIntent(AddEditTradeIntent.DeleteTrade) })
                    }

                    MoreMenu(
                        listOfNotNull(
                            (R.string.duplicate to { onIntent(AddEditTradeIntent.DuplicateTrade) }).takeIf { uiState.withDuplicate },
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
                var nameTextFieldValue by remember { mutableStateOf(TextFieldValue(uiState.name)) }

                TextField(
                    value = nameTextFieldValue,
                    onValueChange = {
                        nameTextFieldValue = it
                        onIntent(AddEditTradeIntent.UpdateName(it.text))
                    },
                    placeholder = { Text(text = stringResource(id = R.string.trade_hint)) },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesters[TradeFieldId.Name.ordinal]),
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
                        value = uiState.amount,
                        onValueChange = {
                            onIntent(AddEditTradeIntent.UpdateAmount(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.amount_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[TradeFieldId.Amount.ordinal]),
                    )

                    TextField(
                        value = uiState.currency,
                        onValueChange = {
                            onIntent(AddEditTradeIntent.UpdateCurrency(it))
                        },
                        placeholder = { Text(stringResource(id = R.string.currency_hint)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequesters[TradeFieldId.Currency.ordinal]),
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.vertical_margin))
                )

                DatePicker(
                    timestamp = uiState.date,
                    onTimestampSelected = {
                        onIntent(AddEditTradeIntent.UpdateDate(it))
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
                            .focusRequester(focusRequesters[TradeFieldId.Date.ordinal])
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
                        onIntent(AddEditTradeIntent.SaveTrade)
                    },
                    modifier = Modifier
                        .responsiveButtonWidth()
                ) {
                    Text(stringResource(R.string.save))
                }

                LaunchedEffect(uiState.focusIndex) {
                    focusRequesters.getOrNull(uiState.focusIndex)?.requestFocus()
                }

                LaunchedEffect(uiState.isLoading, uiState.name) {
                    nameTextFieldValue = TextFieldValue(
                        text = uiState.name,
                        selection = TextRange(uiState.name.length)
                    )

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
                onIntent(AddEditTradeIntent.UserMessageShown)
            }
        }
    }
}

@AllDevicesPreview
@Composable
private fun AddTradeScreenPreview() {
    UrgatTheme {
        Surface {
            AddEditTradeScreen(
                uiState = AddEditTradeUiState(
                    screenTitleResId = R.string.add_trade,
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
private fun EditTradeContentPreview() {
    UrgatTheme {
        Surface {
            AddEditTradeScreen(
                uiState = AddEditTradeUiState(
                    screenTitleResId = R.string.edit_trade,
                    name = "AAPL",
                    amount = "150.00",
                    currency = "USD",
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
private fun LoadingContentPreview() {
    UrgatTheme {
        Surface {
            AddEditTradeScreen(
                uiState = AddEditTradeUiState(
                    screenTitleResId = R.string.edit_trade,
                    isLoading = true,
                ),
                onIntent = {},
                onBack = {},
                modifier = Modifier
            )
        }
    }
}