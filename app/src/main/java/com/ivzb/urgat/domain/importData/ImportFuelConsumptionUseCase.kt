package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.FUEL_CONSUMPTION_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.FuelConsumptionCsvParser.parseFuelConsumptionCsv
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionDao
import com.ivzb.urgat.data.importData.ImportDataRepository
import javax.inject.Inject

interface ImportFuelConsumptionUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importFuelConsumption(content: String)

}

class DefaultImportFuelConsumptionUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val fuelConsumptionDao: FuelConsumptionDao,
): ImportFuelConsumptionUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, FUEL_CONSUMPTION_CSV_FILE_NAME, ::parseFuelConsumptionCsv, fuelConsumptionDao::insertAll)
    }

    override suspend fun importFuelConsumption(content: String) {
        importDataRepository.importString(content, ::parseFuelConsumptionCsv, fuelConsumptionDao::insertAll)
    }

}
