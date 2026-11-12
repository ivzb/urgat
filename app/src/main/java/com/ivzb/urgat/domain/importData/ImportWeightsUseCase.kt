package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.WeightCsvParser.parseWeightCsv
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.weight.WeightDao
import javax.inject.Inject

interface ImportWeightsUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importWeights(content: String)

}

class DefaultImportWeightsUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val weightDao: WeightDao,
): ImportWeightsUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, WEIGHTS_CSV_FILE_NAME, ::parseWeightCsv, weightDao::insertAll)
    }

    override suspend fun importWeights(content: String) {
        importDataRepository.importString(content, ::parseWeightCsv, weightDao::insertAll)
    }

}
