package com.ivzb.urgat.ui.addEditWeight

sealed class AddEditWeightIntent {
    data class UpdateValue(val value: String): AddEditWeightIntent()
    data class UpdateDate(val date: Long): AddEditWeightIntent()
    object SaveWeight: AddEditWeightIntent()
    object DeleteWeight: AddEditWeightIntent()
    object UserMessageShown: AddEditWeightIntent()
}