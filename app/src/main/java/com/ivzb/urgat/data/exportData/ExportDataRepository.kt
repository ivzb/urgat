package com.ivzb.urgat.data.exportData

import android.database.Cursor
import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping

interface ExportDataRepository {

    suspend fun exportCsv(
        uri: Uri,
        cursor: Cursor,
        fileName: String,
        mappings: List<CsvMapping>,
    )

}