package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TRADES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.TradeCsvParser.parseTradeCsv
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.trade.TradeDao
import javax.inject.Inject

interface ImportTradesUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importTrades(content: String)

}

class DefaultImportTradesUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val tradeDao: TradeDao,
): ImportTradesUseCase {

    override suspend operator fun invoke(uri: Uri) {
        importDataRepository.importCsv(uri, TRADES_CSV_FILE_NAME, ::parseTradeCsv, tradeDao::insertAll)
    }

    override suspend fun importTrades(content: String) {
        importDataRepository.importString(content, ::parseTradeCsv, tradeDao::insertAll)
    }

}
