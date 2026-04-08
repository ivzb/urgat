package com.ivzb.urgat.ui.addEditExpense

sealed class AddEditExpenseIntent {
    data class UpdateName(val value: String): AddEditExpenseIntent()
    data class UpdateAmount(val value: String): AddEditExpenseIntent()
    data class UpdateCurrency(val value: String): AddEditExpenseIntent()
    data class UpdateCategory(val value: String): AddEditExpenseIntent()
    data class UpdateDate(val value: Long): AddEditExpenseIntent()
    data class UpdateExcludeFromAnalytics(val value: Boolean): AddEditExpenseIntent()
    object SaveExpense: AddEditExpenseIntent()
    object DeleteExpense: AddEditExpenseIntent()
    object DuplicateExpense: AddEditExpenseIntent()
    object UserMessageShown: AddEditExpenseIntent()
}