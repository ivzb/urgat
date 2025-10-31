package com.ivzb.urgat.ui.addEditExpense

sealed class AddEditExpenseIntent {
    data class UpdateName(val value: String): AddEditExpenseIntent()
    data class UpdateAmount(val value: String): AddEditExpenseIntent()
    data class UpdateCategory(val value: String): AddEditExpenseIntent()
    data class UpdateDate(val value: Long): AddEditExpenseIntent()
    object SaveExpense: AddEditExpenseIntent()
    object UserMessageShown: AddEditExpenseIntent()
}