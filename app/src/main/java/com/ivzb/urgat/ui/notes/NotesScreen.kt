package com.ivzb.urgat.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.navigation.NotesDestination
import com.ivzb.urgat.ui.components.EmptyState
import com.ivzb.urgat.ui.components.LandscapePreview
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun NotesScreen(
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,

    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotesScreenScaffold(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onAddNote = onAddNote,
        onNoteClick = onNoteClick,
        navController = navController,
        modifier = modifier
    )
}

@Composable
private fun NotesScreenScaffold(
    uiState: NotesUiState,
    onSearchQueryChanged: (String) -> Unit,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    MenuScaffold(
        selectedDestination = NotesDestination,
        onSearchQueryChanged = onSearchQueryChanged,
        onAddItem = onAddNote,
        withTopAppBarActions = false,
        navController = navController,
        modifier = modifier
    ) { listState ->

        when {
            uiState.isLoading -> {
                EmptyState(
                    label = uiState.filteringUiInfo.loadingLabel,
                    iconRes = uiState.filteringUiInfo.loadingIconRes
                )
            }

            !uiState.isLoading && uiState.items.isEmpty() -> {
                EmptyState(
                    label = uiState.filteringUiInfo.emptyLabel,
                    iconRes = uiState.filteringUiInfo.emptyIconRes
                )
            }

            uiState.hasError -> {
                EmptyState(
                    label = uiState.filteringUiInfo.errorLabel,
                    iconRes = uiState.filteringUiInfo.errorIconRes
                )
            }

            else -> {
                NotesContent(
                    notes = uiState.items,
                    onNoteClick = onNoteClick,
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
    listState: LazyListState,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.grid_padding))
    ) {

        val (leftNotes, rightNotes) = notes.chunked((notes.size + 1) / 2)
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
                items = listOf(
                    Note(
                        id = "ID 1",
                        content = "Remember to buy milk and eggs before the store closes.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 2",
                        content = "Meeting notes: Discuss project milestones, finalize design specs, and assign tasks for the next sprint. Don’t forget to update the documentation.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 3",
                        content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed euismod, nunc vel tincidunt egestas, justo nulla facilisis mauris, nec rhoncus erat massa vel urna. Curabitur id sagittis mi, in viverra libero. Suspendisse potenti.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 4",
                        content = "Call the electrician tomorrow morning about the kitchen lights — mention the flickering issue.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                    Note(
                        id = "ID 5",
                        content = "Books to read: Atomic Habits, Deep Work, and The Pragmatic Programmer.",
                        isDeleted = false,
                        createdAt = 0,
                    ),
                ),
                filteringUiInfo = NotesFilteringUiInfo(),
                isLoading = false,
                hasError = false
            ),
            onSearchQueryChanged = {},
            onAddNote = {},
            onNoteClick = {},
            navController = rememberNavController(),
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
            onSearchQueryChanged = {},
            onAddNote = {},
            onNoteClick = {},
            navController = rememberNavController(),
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
            onSearchQueryChanged = {},
            onAddNote = {},
            onNoteClick = {},
            navController = rememberNavController(),
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
            onSearchQueryChanged = {},
            onAddNote = {},
            onNoteClick = {},
            navController = rememberNavController(),
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
            notes = listOf(
                Note(
                    id = "ID 1",
                    content = "Remember to buy milk and eggs before the store closes.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 2",
                    content = "Meeting notes: Discuss project milestones, finalize design specs, and assign tasks for the next sprint. Don’t forget to update the documentation.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 3",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed euismod, nunc vel tincidunt egestas, justo nulla facilisis mauris, nec rhoncus erat massa vel urna. Curabitur id sagittis mi, in viverra libero. Suspendisse potenti.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 4",
                    content = "Call the electrician tomorrow morning about the kitchen lights — mention the flickering issue.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 5",
                    content = "Books to read: Atomic Habits, Deep Work, and The Pragmatic Programmer.",
                    isDeleted = false,
                    createdAt = 0,
                )
            ),
            onNoteClick = {},
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
            notes = listOf(
                Note(
                    id = "ID 1",
                    content = "Remember to buy milk and eggs before the store closes.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 2",
                    content = "Meeting notes: Discuss project milestones, finalize design specs, and assign tasks for the next sprint. Don’t forget to update the documentation.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 3",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed euismod, nunc vel tincidunt egestas, justo nulla facilisis mauris, nec rhoncus erat massa vel urna. Curabitur id sagittis mi, in viverra libero. Suspendisse potenti.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 4",
                    content = "Call the electrician tomorrow morning about the kitchen lights — mention the flickering issue.",
                    isDeleted = false,
                    createdAt = 0,
                ),
                Note(
                    id = "ID 5",
                    content = "Books to read: Atomic Habits, Deep Work, and The Pragmatic Programmer.",
                    isDeleted = false,
                    createdAt = 0,
                )
            ),
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
            note = Note(
                id = "ID 1",
                content = "Note 1",
                isDeleted = false,
                createdAt = 0,
            ),
            onNoteClick = {}
        )
    }
}
