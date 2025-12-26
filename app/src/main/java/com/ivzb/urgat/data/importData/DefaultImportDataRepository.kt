package com.ivzb.urgat.data.importData

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import androidx.room.withTransaction
import com.ivzb.urgat.data.source.local.AppDatabase
import com.ivzb.urgat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

class DefaultImportDataRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    private val database: AppDatabase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): ImportDataRepository {

    override suspend fun <T> importCsv(
        uri: Uri,
        fileName: String,
        parseEntity: (String) -> T,
        insertEntities: suspend (List<T>) -> Unit,
    ) {

        return withContext(dispatcher) {
            readFile(
                uri = uri,
                fileName = fileName,
            ) { inputStream ->

                import(inputStream, parseEntity, insertEntities)
            }
        }
    }

    private suspend fun <T> import(
        input: InputStream,
        parseEntity: (String) -> T,
        insertEntities: suspend (List<T>) -> Unit,
    ) {
        val batch = mutableListOf<T>()
        val batchSize = 500

        database.withTransaction {
            readCsv(input) { line ->
                val entity = parseEntity(line)
                batch += entity

                if (batch.size == batchSize) {
                    insertEntities(batch)
                    batch.clear()
                }
            }

            if (batch.isNotEmpty()) {
                insertEntities(batch)
            }
        }
    }

    // handles multiline rows
    private suspend fun readCsv(
        input: InputStream,
        onLine: suspend (String) -> Unit,
    ) {
        input.bufferedReader().useLines { lines ->
            val rowBuilder = StringBuilder()
            var isMultiline = false

            lines.drop(1) // skip header
                .forEach { line ->
                    if (rowBuilder.isNotEmpty()) {
                        rowBuilder.append("\n")
                    }

                    rowBuilder.append(line)

                    if (line.count { it == '"' } % 2 != 0) {
                        isMultiline = !isMultiline
                    }

                    if (!isMultiline) {
                        onLine(rowBuilder.toString())
                        rowBuilder.clear()
                    }
                }

            if (rowBuilder.isNotEmpty()) {
                onLine(rowBuilder.toString())
            }
        }
    }

    private suspend fun readFile(
        uri: Uri,
        fileName: String,
        read: suspend (InputStream) -> Unit
    ) {
        val uri = findFile(contentResolver, uri, fileName)

        if (uri == null) {
            return
        }

        contentResolver.openInputStream(uri)?.use { input ->
            read(input)
        }
    }

    private fun findFile(
        resolver: ContentResolver,
        uri: Uri,
        fileName: String,
    ): Uri? {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri,
            DocumentsContract.getTreeDocumentId(uri)
        )

        resolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
            ),
            null,
            null,
            null
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameCol = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                if (cursor.getString(nameCol) == fileName) {
                    val docId = cursor.getString(idCol)
                    return DocumentsContract.buildDocumentUriUsingTree(uri, docId)
                }
            }
        }

        return null
    }

}