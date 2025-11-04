package com.ivzb.urgat.ui.addEditTodo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.useCase.CreateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.UpdateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.GetTodoUseCase
import com.ivzb.urgat.navigation.AddEditTodoDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val getTodoUseCase: GetTodoUseCase,
    private val createTodoUseCase: CreateTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val todoId: String? = savedStateHandle[AddEditTodoDestination.Args.ARG_ID]

    private val _uiState = MutableStateFlow(AddEditTodoUiState())
    val uiState: StateFlow<AddEditTodoUiState> = _uiState.asStateFlow()

    init {
        if (todoId != null) {
            loadTodo(todoId)
        }
    }

    fun onIntent(intent: AddEditTodoIntent) {
        when (intent) {
            is AddEditTodoIntent.UpdateTitle -> updateTitle(intent.value)
            is AddEditTodoIntent.UpdateDescription -> updateDescription(intent.value)
            AddEditTodoIntent.SaveTodo -> saveTodo()
            AddEditTodoIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    private fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
    }

    private fun saveTodo() {
        if (uiState.value.title.isEmpty() && uiState.value.description.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.empty_todo_message)
            }

            return
        }

        if (todoId == null) {
            createNewTodo()
        } else {
            updateTodo()
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun createNewTodo() = viewModelScope.launch {
        createTodoUseCase(uiState.value.title, uiState.value.description)
        _uiState.update {
            it.copy(isTodoSaved = true)
        }
    }

    private fun updateTodo() {
        if (todoId == null) {
            throw RuntimeException("updateTodo() was called but todo is new.")
        }

        viewModelScope.launch {
            updateTodoUseCase(
                todoId,
                title = uiState.value.title,
                description = uiState.value.description,
            )

            _uiState.update {
                it.copy(isTodoSaved = true)
            }
        }
    }

    private fun loadTodo(todoId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getTodoUseCase(todoId)?.let { todo ->
                if (todo != null) {
                    _uiState.update {
                        it.copy(
                            title = todo.title,
                            description = todo.description,
                            isTodoCompleted = todo.isCompleted,
                            isLoading = false
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