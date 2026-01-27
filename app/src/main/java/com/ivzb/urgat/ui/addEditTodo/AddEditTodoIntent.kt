package com.ivzb.urgat.ui.addEditTodo

sealed class AddEditTodoIntent {
    data class UpdateContent(val value: String): AddEditTodoIntent()
    object SaveTodo: AddEditTodoIntent()
    object DeleteTodo: AddEditTodoIntent()
    object UserMessageShown: AddEditTodoIntent()
}