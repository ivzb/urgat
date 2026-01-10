package com.ivzb.urgat.ui.importData

import android.net.Uri

sealed class ImportDataIntent {
    data class ImportDataFolder(val uri: Uri): ImportDataIntent()

    data object SwitchToManualMode: ImportDataIntent()
    data class UpdateContent(val page: ImportDataPage, val value: String): ImportDataIntent()
    data class ImportDataManually(val page: ImportDataPage): ImportDataIntent()

    data object UserMessageShown: ImportDataIntent()
}
