package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.NoteCsvParser.parseNoteCsv
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.note.NoteDao
import javax.inject.Inject

interface ImportNotesUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importNotes(content: String)

}

class DefaultImportNotesUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val noteDao: NoteDao,
): ImportNotesUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, NOTES_CSV_FILE_NAME, ::parseNoteCsv, noteDao::insertAll)
    }

    override suspend fun importNotes(content: String) {
        importDataRepository.importString(content, ::parseNoteCsv, noteDao::insertAll)
    }

}
