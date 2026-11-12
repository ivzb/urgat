package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.SAVINGS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.SavingCsvParser.parseSavingCsv
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.saving.SavingDao
import javax.inject.Inject

interface ImportSavingsUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importSavings(content: String)

}

class DefaultImportSavingsUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val savingDao: SavingDao,
): ImportSavingsUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, SAVINGS_CSV_FILE_NAME, ::parseSavingCsv, savingDao::insertAll)
    }

    override suspend fun importSavings(content: String) {
        importDataRepository.importString(content, ::parseSavingCsv, savingDao::insertAll)
    }

}
