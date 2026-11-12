package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceDao
import com.ivzb.urgat.data.csv.CarMaintenanceCsvParser.parseCarMaintenanceCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.CAR_MAINTENANCE_CSV_FILE_NAME
import com.ivzb.urgat.data.importData.ImportDataRepository
import javax.inject.Inject

interface ImportCarMaintenanceUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importCarMaintenance(content: String)

}

class DefaultImportCarMaintenanceUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val carMaintenanceDao: CarMaintenanceDao,
): ImportCarMaintenanceUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, CAR_MAINTENANCE_CSV_FILE_NAME, ::parseCarMaintenanceCsv, carMaintenanceDao::insertAll)
    }

    override suspend fun importCarMaintenance(content: String) {
        importDataRepository.importString(content, ::parseCarMaintenanceCsv, carMaintenanceDao::insertAll)
    }

}
