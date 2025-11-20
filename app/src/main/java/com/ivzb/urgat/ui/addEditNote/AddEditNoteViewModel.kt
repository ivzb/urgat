package com.ivzb.urgat.ui.addEditNote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.note.DeleteNoteUseCase
import com.ivzb.urgat.domain.note.GetNoteUseCase
import com.ivzb.urgat.domain.note.UpdateNoteUseCase
import com.ivzb.urgat.navigation.AddEditNoteDestination
import com.ivzb.urgat.util.Assert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val getNoteUseCase: GetNoteUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val noteId: String? = savedStateHandle[AddEditNoteDestination.Args.ARG_ID]

    private val _uiState = MutableStateFlow(AddEditNoteUiState())
    val uiState: StateFlow<AddEditNoteUiState> = _uiState.asStateFlow()

    init {
        if (noteId != null) {
            loadNote(noteId)
        }
    }

    fun onIntent(intent: AddEditNoteIntent) {
        when (intent) {
            is AddEditNoteIntent.UpdateContent -> updateContent(intent.value)
            AddEditNoteIntent.SaveNote -> saveNote()
            AddEditNoteIntent.DeleteNote -> deleteNote()
            AddEditNoteIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateContent(content: String) {
        _uiState.update {
            it.copy(content = content)
        }
    }

    private fun saveNote() {
        val fields = listOf(
            uiState.value.content
        )

        val emptyIndex = fields.indexOfFirst { it.isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_note_message
                )
            }

            return
        }

        if (noteId == null) {
            createNewNote()
        } else {
            updateNote()
        }
    }

    private fun createNewNote() = viewModelScope.launch {
        createNoteUseCase(
            content = uiState.value.content
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateNote() {
        if (noteId == null) {
            Assert.fail("updateNote() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateNoteUseCase(
                noteId,
                content = uiState.value.content
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun deleteNote() {
        if (noteId == null) {
            Assert.fail("deleteNote() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteNoteUseCase(noteId)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun loadNote(noteId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getNoteUseCase(noteId).let { note ->
                if (note != null) {
                    _uiState.update {
                        it.copy(
                            content = note.content,
                            isLoading = false,
                            withDelete = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

}