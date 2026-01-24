package com.ivzb.urgat.ui.addEditSaving

sealed class AddEditSavingIntent {
    data class UpdateName(val value: String): AddEditSavingIntent()
    data class UpdateAmount(val value: String): AddEditSavingIntent()
    data class UpdateDate(val value: Long): AddEditSavingIntent()
    object SaveSaving: AddEditSavingIntent()
    object DeleteSaving: AddEditSavingIntent()
    object UserMessageShown: AddEditSavingIntent()
}
