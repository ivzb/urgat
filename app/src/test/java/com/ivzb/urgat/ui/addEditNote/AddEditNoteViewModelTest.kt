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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditNoteViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var getNoteUseCase: FakeGetNoteUseCase

    private lateinit var createNoteUseCase: FakeCreateNoteUseCase

    private lateinit var updateNoteUseCase: FakeUpdateNoteUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        getNoteUseCase = FakeGetNoteUseCase()
        createNoteUseCase = FakeCreateNoteUseCase()
        updateNoteUseCase = FakeUpdateNoteUseCase()
        savedStateHandle = SavedStateHandle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load note`() = runTest {
        val viewModel = createViewModel()

        Assert.assertNull(getNoteUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads note`() = runTest {
        val note = Note("123", "Note", false, 123)

        getNoteUseCase = FakeGetNoteUseCase(note)
        savedStateHandle[AddEditNoteDestination.Args.ARG_ID] = "123"

        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        Assert.assertEquals("Note", state.content)
        assertFalse(state.isLoading)
        Assert.assertEquals("123", getNoteUseCase.calledWithId)
    }

    @Test
    fun `updateContent updates ui state`() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AddEditNoteIntent.UpdateContent("New Content"))
        Assert.assertEquals("New Content", viewModel.uiState.value.content)
    }

    @Test
    fun `saveNote with empty fields shows message`() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AddEditNoteIntent.SaveNote)
        assertEquals(R.string.empty_note_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `createNewNote calls create usecase and sets isNoteSaved`() = runTest {
        val viewModel = createViewModel()

        viewModel.onIntent(AddEditNoteIntent.UpdateContent("Note"))
        viewModel.onIntent(AddEditNoteIntent.SaveNote)

        advanceUntilIdle()

        assertEquals(listOf("Note"), createNoteUseCase.created)
        assertTrue(viewModel.uiState.value.isNoteSaved)
    }

    @Test
    fun `updateNote calls update usecase and sets isNoteSaved`() = runTest {
        val noteId = "abc"
        savedStateHandle[AddEditNoteDestination.Args.ARG_ID] = noteId

        val note = Note(noteId, "Old", false, 123)
        getNoteUseCase = FakeGetNoteUseCase(note)

        val viewModel = createViewModel()

        advanceUntilIdle()

        viewModel.onIntent(AddEditNoteIntent.UpdateContent("Updated"))
        viewModel.onIntent(AddEditNoteIntent.SaveNote)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isNoteSaved)
        assertEquals(listOf(Pair(noteId, "Updated")), updateNoteUseCase.updated)
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        val viewModel = createViewModel()
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
