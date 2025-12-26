package com.ivzb.urgat.ui.exportData

import android.net.Uri

sealed class ExportDataIntent {
    data class ExportDataData(val uri: Uri): ExportDataIntent()
    data object UserMessageShown: ExportDataIntent()
}
