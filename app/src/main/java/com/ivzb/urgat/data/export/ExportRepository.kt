package com.ivzb.urgat.data.export

import android.database.Cursor
import android.net.Uri

interface ExportRepository {

    suspend fun exportCsv(
        uri: Uri,
        cursor: Cursor,
        fileName: String,
        mappings: List<ExportMapping>,
    )

}