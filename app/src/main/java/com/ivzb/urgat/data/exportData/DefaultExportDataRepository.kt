package com.ivzb.urgat.data.exportData

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.database.getStringOrNull
import com.ivzb.urgat.data.csv.CsvMapping
import com.ivzb.urgat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.OutputStream
import javax.inject.Inject

class DefaultExportDataRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): ExportDataRepository {

    override suspend fun exportCsv(
        uri: Uri,
        cursor: Cursor,
        fileName: String,
        mappings: List<CsvMapping>,
    ) {
        return withContext(dispatcher) {
            writeFile(
                resolver = contentResolver,
                uri = uri,
                fileName = fileName,
                mimeType = CSV_MIME,
            ) { outputStream ->

                export(outputStream, cursor, mappings)
            }
        }
    }

    private fun export(outputStream: OutputStream, cursor: Cursor, mappings: List<CsvMapping>) {
        outputStream.bufferedWriter().use { writer ->
            val columns = mappings.map { it.column }
            val types = mappings.map { it.type }

            val headers = columns.joinToString(",")
            writer.appendLine(headers)

            cursor.use { cursor ->
                val indexes = columns.map { column -> cursor.getColumnIndexOrThrow(column) }

                while (cursor.moveToNext()) {
                    val data = indexes.mapIndexed { index, columnIndex ->
                        when (types[index]) {
                            "string" -> "\"${cursor.getString(columnIndex).replace('"', '\'')}\""
                            "string?" -> {
                                val column = cursor.getStringOrNull(columnIndex)

                                if (column != null) {
                                    "\"$column\""
                                } else {
                                    ""
                                }
                            }
                            "big_decimal" -> "\"${cursor.getString(columnIndex)}\""
                            "big_decimal?" -> {
                                val column = cursor.getStringOrNull(columnIndex)

                                if (column != null) {
                                    "\"$column\""
                                } else {
                                    ""
                                }
                            }
                            "boolean" -> "${cursor.getInt(columnIndex)}"
                            "int" -> "${cursor.getInt(columnIndex)}"
                            "long" -> "${cursor.getLong(columnIndex)}"
                            else -> throw Exception("unsupported type ${types[index]}")
                        }
                    }

                    writer.appendLine(data.joinToString(","))
                }
            }
        }
    }

    private inline fun writeFile(
        resolver: ContentResolver,
        uri: Uri,
        fileName: String,
        mimeType: String,
        write: (OutputStream) -> Unit
    ) {
        val fileUri = createOrReplaceFile(
            resolver = resolver,
            uri = uri,
            fileName = fileName,
            mimeType = mimeType,
        )

        resolver.openOutputStream(fileUri, "w")?.use { output ->
            write(output)
        } ?: error("Unable to open output stream for $fileName")
    }

    /**
     * SAF-safe file creation that replaces existing files.
     */
    private fun createOrReplaceFile(
        resolver: ContentResolver,
        uri: Uri,
        fileName: String,
        mimeType: String
    ): Uri {
        // Delete existing file if present
        findExistingFile(resolver, uri, fileName)?.let {
            DocumentsContract.deleteDocument(resolver, it)
        }

        val parentDocumentUri = DocumentsContract.buildDocumentUriUsingTree(
            uri,
            DocumentsContract.getTreeDocumentId(uri)
        )

        return DocumentsContract.createDocument(
            resolver,
            parentDocumentUri,
            mimeType,
            fileName
        ) ?: error("Failed to create document: $fileName")
    }

    private fun findExistingFile(
        resolver: ContentResolver,
        uri: Uri,
        fileName: String
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
            val idCol = cursor.getColumnIndexOrThrow(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID
            )
            val nameCol = cursor.getColumnIndexOrThrow(
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
            )

            while (cursor.moveToNext()) {
                if (cursor.getString(nameCol) == fileName) {
                    val docId = cursor.getString(idCol)
                    return DocumentsContract.buildDocumentUriUsingTree(
                        uri,
                        docId
                    )
                }
            }
        }

        return null
    }

    companion object {

        private const val CSV_MIME = "text/csv"

    }

}