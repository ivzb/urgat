package com.ivzb.urgat.ui.importData

import android.net.Uri

sealed class ImportDataIntent {
    data class ImportData(val uri: Uri): ImportDataIntent()
    data object UserMessageShown: ImportDataIntent()
}
