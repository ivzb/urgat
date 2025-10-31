package com.ivzb.urgat.ui.addEditTodo

sealed class AddEditTodoIntent {
    data class UpdateTitle(val value: String): AddEditTodoIntent()
    data class UpdateDescription(val value: String): AddEditTodoIntent()
    object SaveTodo: AddEditTodoIntent()
    object UserMessageShown: AddEditTodoIntent()
}