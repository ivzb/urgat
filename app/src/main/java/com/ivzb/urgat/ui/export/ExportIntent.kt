package com.ivzb.urgat.ui.export

import android.net.Uri

sealed class ExportIntent {
    data class ExportData(val uri: Uri): ExportIntent()
    data object UserMessageShown: ExportIntent()
}
