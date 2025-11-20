package com.ivzb.urgat.ui.addEditInvestment

sealed class AddEditInvestmentIntent {
    data class UpdateName(val value: String): AddEditInvestmentIntent()
    data class UpdateCurrency(val value: String): AddEditInvestmentIntent()
    data class UpdateAmount(val value: String): AddEditInvestmentIntent()
    data class UpdateCategory(val value: String): AddEditInvestmentIntent()
    object SaveInvestment: AddEditInvestmentIntent()
    object DeleteInvestment: AddEditInvestmentIntent()
    object UserMessageShown: AddEditInvestmentIntent()
}
