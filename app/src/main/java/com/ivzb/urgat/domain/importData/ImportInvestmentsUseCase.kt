package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.InvestmentCsvParser.parseInvestmentCsv
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.investment.InvestmentDao
import javax.inject.Inject

interface ImportInvestmentsUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importInvestments(content: String)

}

class DefaultImportInvestmentsUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val investmentDao: InvestmentDao,
): ImportInvestmentsUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, INVESTMENTS_CSV_FILE_NAME, ::parseInvestmentCsv, investmentDao::insertAll)
    }

    override suspend fun importInvestments(content: String) {
        importDataRepository.importString(content, ::parseInvestmentCsv, investmentDao::insertAll)
    }

}
