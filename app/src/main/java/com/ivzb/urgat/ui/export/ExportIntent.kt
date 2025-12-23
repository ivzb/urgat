package com.ivzb.urgat.ui.export

sealed class ExportIntent {
    data object ExportData: ExportIntent()
    data object UserMessageShown: ExportIntent()
}
