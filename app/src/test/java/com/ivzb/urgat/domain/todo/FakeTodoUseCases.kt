package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.todos.TodosFilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FakeObserveTodosUseCase : ObserveTodosUseCase {

    private val flow = MutableStateFlow<Async<List<Todo>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<Todo>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        filterTypeFlow: Flow<TodosFilterType>?,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Todo>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeGetTodoUseCase : GetTodoUseCase {

    var toReturn: Todo? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Todo? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateTodoUseCase : CreateTodoUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(name: String, description: String) {
        created = listOf(name, description)
    }
}

class FakeUpdateTodoUseCase : UpdateTodoUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(id: String, title: String, description: String) {
        updated = listOf(id, title, description)
    }
}


class FakeCompleteTodoUseCase : CompleteTodoUseCase {

    var completedId: String? = null

    override suspend fun invoke(id: String) {
        completedId = id
    }
}

class FakeActivateTodoUseCase : ActivateTodoUseCase {

    var activatedId: String? = null

    override suspend fun invoke(id: String) {
        activatedId = id
    }
}

class FakeClearCompletedTodosUseCase : ClearCompletedTodosUseCase {

    var cleared: Boolean = false

    override suspend fun invoke() {
        cleared = true
    }
}

class FakeObserveTodosFilteringUseCase : ObserveTodosFilteringUseCase {

    private val flow = MutableStateFlow<Async<List<TodosFilterMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<TodosFilterMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<TodosFilterMenuItem>>> {
        return flow
    }

}

class FakeDeleteTodoUseCase : DeleteTodoUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}
