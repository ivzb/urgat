package com.ivzb.urgat.ui.todos

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator
import com.ivzb.urgat.navigation.UrgatNavActions

fun EntryProviderScope<NavKey>.todosEntry(navigator: Navigator, navActions: UrgatNavActions) {
    entry<TodosRoute> {
        TodosScreen(
            viewModel = hiltViewModel(),

            onAddTodo = { navActions.navigateToAddTodo() },
            onTodoClick = { navActions.navigateToEditTodo(it.id) },
            onBack = { navigator.goBack() }
        )
    }
}