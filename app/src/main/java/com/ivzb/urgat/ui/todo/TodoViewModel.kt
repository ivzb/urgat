package com.ivzb.urgat.ui.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class TodoUiState(
    val items: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
//    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo()
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


}