package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WATCHLIST_ASSETS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.WatchlistAssetCsvParser.parseWatchlistAssetCsv
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.watchlist.WatchlistAssetDao
import javax.inject.Inject

interface ImportWatchlistAssetsUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importWatchlistAssets(content: String)

}

class DefaultImportWatchlistAssetsUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val watchlistAssetDao: WatchlistAssetDao,
): ImportWatchlistAssetsUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, WATCHLIST_ASSETS_CSV_FILE_NAME, ::parseWatchlistAssetCsv, watchlistAssetDao::insertAll)
    }

    override suspend fun importWatchlistAssets(content: String) {
        importDataRepository.importString(content, ::parseWatchlistAssetCsv, watchlistAssetDao::insertAll)
    }

}
