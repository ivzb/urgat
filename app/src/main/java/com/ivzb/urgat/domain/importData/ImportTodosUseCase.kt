package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.TodoCsvParser.parseTodoCsv
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.todo.TodoDao
import javax.inject.Inject

interface ImportTodosUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importTodos(content: String)

}

class DefaultImportTodosUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val todoDao: TodoDao,
): ImportTodosUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, TODOS_CSV_FILE_NAME, ::parseTodoCsv, todoDao::insertAll)
    }

    override suspend fun importTodos(content: String) {
        importDataRepository.importString(content, ::parseTodoCsv, todoDao::insertAll)
    }

}
