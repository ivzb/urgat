package com.ivzb.urgat.ui.addEditTodo

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.ivzb.urgat.navigation.Navigator

fun EntryProviderScope<NavKey>.addEditTodoEntry(navigator: Navigator) {
    entry<AddEditTodoRoute> { route ->
        val viewModel = hiltViewModel<AddEditTodoViewModel, AddEditTodoViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(route)
            }
        )

        AddEditTodoScreen(
            viewModel = viewModel,

            onBack = { navigator.goBack() },
        )
    }
}