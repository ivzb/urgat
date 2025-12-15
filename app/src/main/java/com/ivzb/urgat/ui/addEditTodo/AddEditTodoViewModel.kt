package com.ivzb.urgat.ui.addEditTodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import com.ivzb.urgat.domain.todo.DeleteTodoUseCase
import com.ivzb.urgat.domain.todo.UpdateTodoUseCase
import com.ivzb.urgat.domain.todo.GetTodoUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.sanitize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditTodoViewModel.Factory::class)
class AddEditTodoViewModel @AssistedInject constructor(
    @Assisted val route: AddEditTodoRoute,
    private val getTodoUseCase: GetTodoUseCase,
    private val createTodoUseCase: CreateTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private val todoId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditTodoUiState(
        screenTitleResId = screenTitleResId,
    ))
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
            AddEditTodoIntent.DeleteTodo -> deleteTodo()
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
        if (uiState.value.title.sanitize().isEmpty() && uiState.value.description.sanitize().isEmpty()) {
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

    private fun createNewTodo() = viewModelScope.launch {
        createTodoUseCase(
            name = uiState.value.title.sanitize(),
            description = uiState.value.description.sanitize()
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateTodo() {
        if (todoId == null) {
            Assert.fail("updateTodo() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateTodoUseCase(
                todoId,
                title = uiState.value.title.sanitize(),
                description = uiState.value.description.sanitize(),
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun deleteTodo() {
        if (todoId == null) {
            Assert.fail("deleteTodo() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteTodoUseCase(todoId)

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

    private fun loadTodo(todoId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getTodoUseCase(todoId).let { todo ->
                if (todo != null) {
                    _uiState.update {
                        it.copy(
                            title = todo.title,
                            description = todo.description,
                            isTodoCompleted = todo.isCompleted,
                            isLoading = false,
                            withDelete = true,
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

    @AssistedFactory
    interface Factory {
        fun create(route: AddEditTodoRoute): AddEditTodoViewModel
    }

}