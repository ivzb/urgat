package com.ivzb.urgat.ui.notes

import androidx.annotation.VisibleForTesting
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.source.sample.SampleData
import com.ivzb.urgat.ui.components.AllDevicesPreview
import com.ivzb.urgat.ui.listDetail.ListDetailScreen
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun NotesScreen(
    viewModel: NotesViewModel,

    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotesScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddNote = onAddNote,
        onNoteClick = onNoteClick,
        onBack = onBack,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
fun NotesScreen(
    uiState: NotesUiState,
    onIntent: (NotesIntent) -> Unit,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagesSize = 2
    val listStates = remember { List(pagesSize) { LazyListState() } }

    ListDetailScreen(
        uiState = uiState,

        route = NotesRoute,
        onSearchQueryChanged = { query -> onIntent(NotesIntent.SearchQueryChanged(query)) },
        onAddItem = onAddNote,
        onBack = onBack,

        listState = listStates[1],

        content = {
            NotesContent(
                notes = uiState.items,
                onNoteClick = onNoteClick,
                listStates = listStates,
            )
        },

        modifier = modifier,
    )
}

@Composable
internal fun NotesContent(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    listStates: List<LazyListState>,
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
            listStates[0],
            modifier = Modifier.weight(1f)
        )

        NotesColumn(
            rightNotes,
            onNoteClick,
            listStates[1],
            modifier = Modifier.weight(1f)
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
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        modifier = modifier
    ) {
        items(
            items = notes,
            key = { note -> note.id },
        ) { note ->
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

@AllDevicesPreview
@Composable
private fun NotesScreenPreview() {
    UrgatTheme {
        NotesScreen(
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

@AllDevicesPreview
@Composable
private fun NotesScreenEmptyPreview() {
    UrgatTheme {
        NotesScreen(
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

@AllDevicesPreview
@Composable
private fun NotesScreenLoadingPreview() {
    UrgatTheme {
        NotesScreen(
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

@AllDevicesPreview
@Composable
private fun NotesScreenErrorPreview() {
    UrgatTheme {
        NotesScreen(
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