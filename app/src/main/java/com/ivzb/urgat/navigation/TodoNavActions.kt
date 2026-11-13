package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoRoute

fun UrgatNavActions.navigateToAddTodo() {
    navigator.navigate(
        AddEditTodoRoute(
            id = null,
            titleResId = R.string.add_todo
        )
    )
}

fun UrgatNavActions.navigateToEditTodo(todoId: String) {
    navigator.navigate(
        AddEditTodoRoute(
            id = todoId,
            titleResId = R.string.edit_todo
        )
    )
}
