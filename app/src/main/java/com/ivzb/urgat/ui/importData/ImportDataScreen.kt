package com.ivzb.urgat.ui.importData

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.csv.CsvMapping.Companion.format
import com.ivzb.urgat.ui.components.DarkModePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.TextTopAppBar
import com.ivzb.urgat.ui.components.rememberDebouncedClick
import com.ivzb.urgat.ui.components.responsiveButtonWidth
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

@Composable
fun ImportDataScreen(
    viewModel: ImportDataViewModel,

    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ImportDataScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun ImportDataScreen(
    uiState: ImportDataUiState,
    onIntent: (ImportDataIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val pages = IMPORT_DATA_PAGES
    val pagerState = rememberPagerState(initialPage = uiState.initialPage) { pages.size }

    Scaffold(
        topBar = {
            TextTopAppBar(
                title = R.string.import_data,
                onBack = onBack,
                withDelete = false,
                onDelete = {},
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
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            when {
                uiState.isLoading -> {
                    LoadingState(
                        label = R.string.loading,
                        modifier = Modifier.weight(1f)
                    )
                }

                uiState.isManualMode -> {
                    ImportDataManualContent(
                        uiState = uiState,
                        onIntent = onIntent,
                        pages = pages,
                        pagerState = pagerState,
                    )
                }

                else -> {
                    ImportDataContent(
                        onIntent = onIntent,
                    )
                }
            }
        }
    }

    uiState.userMessage?.let { userMessage ->
        val snackbarText = stringResource(userMessage)

        LaunchedEffect(userMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(snackbarText)
                onIntent(ImportDataIntent.UserMessageShown)
            }
        }
    }
}

@Composable
private fun ImportDataContent(
    onIntent: (ImportDataIntent) -> Unit,
) {
    val context = LocalContext.current

    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            onIntent(ImportDataIntent.ImportDataFolder(it))
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                folderPicker.launch(null)
            },
        ) {

            Text(stringResource(R.string.import_button_text_folder))
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_margin)))

        Button(
            onClick = {
                onIntent(ImportDataIntent.SwitchToManualMode)
            },
        ) {

            Text(stringResource(R.string.import_button_text_manually))
        }
    }
}

@Composable
private fun ImportDataManualContent(
    uiState: ImportDataUiState,
    onIntent: (ImportDataIntent) -> Unit,
    pages: List<ImportDataPage>,
    pagerState: PagerState,
) {
    val scope = rememberCoroutineScope()

    Column {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    text = {
                        Text(
                            text = stringResource(page.titleResId),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = dimensionResource(R.dimen.content_padding_vertical)),
        ) { pageIndex ->

            ImportDataManualPage(
                page = pages[pageIndex],
                uiState = uiState,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun ImportDataManualPage(
    uiState: ImportDataUiState,
    onIntent: (ImportDataIntent) -> Unit,
    page: ImportDataPage,
) {
    val mapping = uiState.manualDataMapping[page]?.format().orEmpty()
    val example = uiState.manualDataExample[page]?.format().orEmpty()
    val content: String = uiState.manualDataContent[page].orEmpty()

    Column {
        Text(stringResource(R.string.import_data_mapping, mapping))

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
        )

        Text(stringResource(R.string.import_data_example, example))

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
        )

        TextField(
            value = content,
            onValueChange = {
                onIntent(ImportDataIntent.UpdateContent(page, it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default // allow multiline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )

        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
        )

        Button(
            onClick = rememberDebouncedClick {
                onIntent(ImportDataIntent.ImportDataManually(page))
            },
            modifier = Modifier
                .responsiveButtonWidth()
        ) {
            Text(stringResource(R.string.save))
        }
    }
}

@Preview
@DarkModePreview
@Composable
private fun ImportScreenPreview() {
    UrgatTheme {
        ImportDataScreen(
            uiState = ImportDataUiState(
                isLoading = false,
            ),
            onIntent = {},
            onBack = {},
            modifier = Modifier,
        )
    }
}

@Preview
@DarkModePreview
@Composable
private fun ImportScreenLoadingPreview() {
    UrgatTheme {
        ImportDataScreen(
            uiState = ImportDataUiState(
                isLoading = true,
            ),
            onIntent = {},
            onBack = {},
            modifier = Modifier,
        )
    }
}
