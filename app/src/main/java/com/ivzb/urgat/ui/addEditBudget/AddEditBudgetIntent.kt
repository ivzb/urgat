package com.ivzb.urgat.ui.addEditBudget

sealed class AddEditBudgetIntent {
    data class UpdateIncome(val value: String): AddEditBudgetIntent()
    data class UpdateBankStart(val value: String): AddEditBudgetIntent()
    data class UpdateBankEnd(val value: String): AddEditBudgetIntent()
    data class UpdateDate(val value: Long): AddEditBudgetIntent()
    object SaveBudget: AddEditBudgetIntent()
    object DeleteBudget: AddEditBudgetIntent()
    object UserMessageShown: AddEditBudgetIntent()
}