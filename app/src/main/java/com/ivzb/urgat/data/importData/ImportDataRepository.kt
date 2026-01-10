package com.ivzb.urgat.data.importData

import android.net.Uri

interface ImportDataRepository {

    suspend fun <T> importCsv(
        uri: Uri,
        fileName: String,
        parseEntity: (String) -> T,
        insertEntities: suspend (List<T>) -> Unit,
    )

    suspend fun <T> importString(
        content: String,
        parseEntity: (String) -> T,
        insertEntities: suspend (List<T>) -> Unit,
    )

}