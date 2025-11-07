package com.ivzb.urgat.ui.addEditNote

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.domain.notes.FakeCreateNoteUseCase
import com.ivzb.urgat.domain.notes.FakeGetNoteUseCase
import com.ivzb.urgat.domain.notes.FakeUpdateNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.navigation.AddEditNoteDestination
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditNoteViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var getNoteUseCase: FakeGetNoteUseCase

    private lateinit var createNoteUseCase: FakeCreateNoteUseCase

    private lateinit var updateNoteUseCase: FakeUpdateNoteUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: AddEditNoteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        getNoteUseCase = FakeGetNoteUseCase()
        createNoteUseCase = FakeCreateNoteUseCase()
        updateNoteUseCase = FakeUpdateNoteUseCase()
        savedStateHandle = SavedStateHandle()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load note`() = runTest {
        assertNull(getNoteUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads note`() = runTest {
        val noteId = "123"
        val note = Note(
            id = noteId,
            content = "Note",
            isDeleted = false,
            createdAt = 123
        )

        getNoteUseCase.toReturn = note
        savedStateHandle[AddEditNoteDestination.Args.ARG_ID] = noteId

        viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Note", state.content)
        assertFalse(state.isLoading)
        assertEquals("123", getNoteUseCase.calledWithId)
    }

    @Test
    fun `updateContent updates ui state`() = runTest {
        viewModel.onIntent(AddEditNoteIntent.UpdateContent("New Content"))
        Assert.assertEquals("New Content", viewModel.uiState.value.content)
    }

    @Test
    fun `saveNote with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditNoteIntent.SaveNote)
        assertEquals(R.string.empty_note_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveNote creates new note`() = runTest {
        viewModel.onIntent(AddEditNoteIntent.UpdateContent("Note"))
        viewModel.onIntent(AddEditNoteIntent.SaveNote)

        advanceUntilIdle()

        val created = createNoteUseCase.created
        assertEquals("Note", created)
        assertTrue(viewModel.uiState.value.isNoteSaved)
    }

    @Test
    fun `saveNote updates existing note`() = runTest {
        val noteId = "abc"
        savedStateHandle[AddEditNoteDestination.Args.ARG_ID] = noteId

        val note = Note(
            id = noteId,
            content = "Old",
            isDeleted = false,
            createdAt = 123
        )
        getNoteUseCase.toReturn = note

        viewModel = createViewModel()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(note.content, uiState.content)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditNoteIntent.UpdateContent("Updated"))
        viewModel.onIntent(AddEditNoteIntent.SaveNote)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isNoteSaved)
        assertEquals(Pair(noteId, "Updated"), updateNoteUseCase.updated)
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditNoteIntent.SaveNote)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditNoteIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel() = AddEditNoteViewModel(
        getNoteUseCase,
        createNoteUseCase,
        updateNoteUseCase,
        savedStateHandle
    )
}
