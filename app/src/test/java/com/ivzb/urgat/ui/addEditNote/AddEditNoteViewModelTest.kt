package com.ivzb.urgat.ui.addEditNote

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.domain.note.FakeCreateNoteUseCase
import com.ivzb.urgat.domain.note.FakeGetNoteUseCase
import com.ivzb.urgat.domain.note.FakeUpdateNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.domain.note.FakeDeleteNoteUseCase
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
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditNoteViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetNoteUseCase

    private lateinit var createUseCase: FakeCreateNoteUseCase

    private lateinit var updateUseCase: FakeUpdateNoteUseCase

    private lateinit var deleteUseCase: FakeDeleteNoteUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: AddEditNoteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        getUseCase = FakeGetNoteUseCase()
        createUseCase = FakeCreateNoteUseCase()
        updateUseCase = FakeUpdateNoteUseCase()
        deleteUseCase = FakeDeleteNoteUseCase()
        savedStateHandle = SavedStateHandle()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load note`() = runTest {
        assertNull(getUseCase.calledWithId)
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

        getUseCase.toReturn = note
        savedStateHandle[AddEditNoteDestination.Args.ARG_ID] = noteId

        viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Note", state.content)
        assertFalse(state.isLoading)
        assertEquals("123", getUseCase.calledWithId)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
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

        val created = createUseCase.created
        assertEquals("Note", created)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
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
        getUseCase.toReturn = note

        viewModel = createViewModel()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(note.content, uiState.content)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditNoteIntent.UpdateContent("Updated"))
        viewModel.onIntent(AddEditNoteIntent.SaveNote)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.shouldNavigateBack)
        val (id, content) = updateUseCase.updated
        assertEquals(noteId, id)
        assertEquals("Updated", content)
    }

    @Test
    fun `deleteNote deletes existing note`() = runTest {
        val noteId = "123"
        savedStateHandle[AddEditNoteDestination.Args.ARG_ID] = noteId

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(AddEditNoteIntent.DeleteNote)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(noteId, id)
    }

    @Test
    fun `deleteNote deletes non-existing note`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditNoteIntent.DeleteNote)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditNoteIntent.SaveNote)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditNoteIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel() = AddEditNoteViewModel(
        getUseCase,
        createUseCase,
        updateUseCase,
        deleteUseCase,
        savedStateHandle
    )
}
