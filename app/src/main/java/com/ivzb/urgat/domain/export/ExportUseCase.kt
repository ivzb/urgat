package com.ivzb.urgat.domain.export

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.data.weight.WeightsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import javax.inject.Inject

interface ExportUseCase {

    suspend operator fun invoke(uri: Uri, resolver: ContentResolver)

}

class DefaultExportUseCase @Inject constructor(
    private val todoRepository: TodoRepository,
    private val notesRepository: NotesRepository,
    private val expensesRepository: ExpensesRepository,
    private val investmentsRepository: InvestmentsRepository,
    private val weightsRepository: WeightsRepository,
): ExportUseCase {

    override suspend operator fun invoke(uri: Uri, resolver: ContentResolver) {
        exportFile(
            resolver = resolver,
            uri = uri,
            fileName = TODOS_FILE,
        ) { output ->
            exportTodos(output)
        }

        // todo: export to csv
    }

    suspend fun exportTodos(outputStream: OutputStream) {
        return withContext(Dispatchers.IO) {
            outputStream.bufferedWriter().use { writer ->
                writer.appendLine("id,title,description,isCompleted,createdAt")

                todoRepository.stream().use { cursor ->
                    val idIndex = cursor.getColumnIndexOrThrow("id")
                    val titleIndex = cursor.getColumnIndexOrThrow("title")
                    val descriptionIndex = cursor.getColumnIndexOrThrow("description")
                    val isCompletedIndex = cursor.getColumnIndexOrThrow("isCompleted")
                    val createdAtIndex = cursor.getColumnIndexOrThrow("createdAt")

                    while (cursor.moveToNext()) {
                        writer.appendLine(
                            "\"${cursor.getString(idIndex)}\"," +
                                    "\"${cursor.getString(titleIndex)}\"," +
                                    "\"${cursor.getString(descriptionIndex)}\"," +
                                    "\"${cursor.getString(descriptionIndex)}\"," +
                                    "${cursor.getInt(isCompletedIndex)}," +
                                    "${cursor.getLong(createdAtIndex)}"
                        )
                    }
                }
            }
        }
    }

    private inline fun exportFile(
        resolver: ContentResolver,
        uri: Uri,
        fileName: String,
        write: (OutputStream) -> Unit
    ) {
        val fileUri = createOrReplaceFile(
            resolver = resolver,
            uri = uri,
            fileName = fileName,
            mimeType = CSV_MIME
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

        private const val TODOS_FILE = "todos.csv"

    }
}
