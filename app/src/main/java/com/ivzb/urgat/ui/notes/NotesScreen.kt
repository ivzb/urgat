package com.ivzb.urgat.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.navigation.NotesDestination
import com.ivzb.urgat.ui.components.EmptyStateScreen
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.LoadingState
import com.ivzb.urgat.ui.components.UrgatScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun NotesScreen(
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotesScreenScaffold(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddNote = onAddNote,
        onNoteClick = onNoteClick,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun NotesScreenScaffold(
    uiState: NotesUiState,
    onIntent: (NotesIntent) -> Unit,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    UrgatScaffold(
        selectedDestination = NotesDestination,
        onSearchQueryChanged = { query -> onIntent(NotesIntent.SearchQueryChanged(query)) },
        onAddItem = onAddNote,
        onBack = onBack,
        modifier = modifier
    ) { listState ->

        when {
            uiState.isLoading -> {
                LoadingState(
                    label = uiState.filteringUiInfo.loadingLabel,
                )
            }

            uiState.hasError -> {
                EmptyStateScreen(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes
                )
            }

            else -> {
                NotesContent(
                    notes = uiState.items,
                    onNoteClick = onNoteClick,
                    filteringUiInfo = uiState.filteringUiInfo,
                    listState = listState
                )
            }
        }
    }
}

@Composable
internal fun NotesContent(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    filteringUiInfo: NotesFilteringUiInfo,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.grid_padding))
    ) {

        val chunkSize = if (notes.isEmpty()) 1 else (notes.size + 1) / 2
        val (leftNotes, rightNotes) = notes.chunked(chunkSize)
            .let { it.getOrNull(0).orEmpty() to it.getOrNull(1).orEmpty() }

        NotesColumn(
            leftNotes,
            onNoteClick,
            listState,
            modifier = Modifier.weight(1f)
        )

        NotesColumn(
            rightNotes,
            onNoteClick,
            listState,
            modifier = Modifier.weight(1f)
        )
    }

    if (notes.isEmpty()) {
        EmptyStateScreen(
            label = filteringUiInfo.emptyLabel,
            iconRes = filteringUiInfo.emptyIconRes
        )
    }

    Spacer(modifier = Modifier
        .height(dimensionResource(R.dimen.vertical_margin))
    )
}

@Composable
private fun NotesColumn(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_vertical_margin)),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.grid_horizontal_margin),
            vertical = dimensionResource(R.dimen.grid_vertical_margin)
        ),
        modifier = modifier
    ) {
        items(notes) { note ->
            NoteItem(
                note = note,
                onNoteClick = onNoteClick
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onNoteClick: (Note) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNoteClick(note) }
                .padding(dimensionResource(R.dimen.list_item_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@LandscapePreview
@Composable
private fun NotesScreenScaffoldPreview() {
    UrgatTheme {
        NotesScreenScaffold(
            uiState = NotesUiState(
                items = SampleData.SAMPLE_NOTES,
                filteringUiInfo = NotesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddNote = {},
            onNoteClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun NotesScreenScaffoldEmptyPreview() {
    UrgatTheme {
        NotesScreenScaffold(
            uiState = NotesUiState(
                items = listOf(),
                filteringUiInfo = NotesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onIntent = {},
            onAddNote = {},
            onNoteClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun NotesScreenScaffoldLoadingPreview() {
    UrgatTheme {
        NotesScreenScaffold(
            uiState = NotesUiState(
                items = listOf(),
                filteringUiInfo = NotesFilteringUiInfo(),
                isLoading = true,
                hasError = false
            ),
            onIntent = {},
            onAddNote = {},
            onNoteClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun NotesScreenScaffoldErrorPreview() {
    UrgatTheme {
        NotesScreenScaffold(
            uiState = NotesUiState(
                items = listOf(),
                filteringUiInfo = NotesFilteringUiInfo(),
                isLoading = false,
                hasError = true
            ),
            onIntent = {},
            onAddNote = {},
            onNoteClick = {},
            onBack = {},
            modifier = Modifier
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun NotesScreenContentPreview() {
    UrgatTheme {
        NotesContent(
            notes = SampleData.SAMPLE_NOTES,
            onNoteClick = {},
            filteringUiInfo = NotesFilteringUiInfo(),
            listState = rememberLazyListState()
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun NotesColumnPreview() {
    UrgatTheme {
        NotesColumn(
            notes = SampleData.SAMPLE_NOTES,
            onNoteClick = {},
            listState = rememberLazyListState()
        )
    }
}

@Preview
@LandscapePreview
@Composable
private fun NoteItemPreview() {
    UrgatTheme {
        NoteItem(
            note = SampleData.SAMPLE_NOTES.first(),
            onNoteClick = {}
        )
    }
}
