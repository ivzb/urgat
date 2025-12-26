package com.ivzb.urgat.domain.exportData

import android.net.Uri

class FakeExportUseCase : ExportDataUseCase {

    var invoked: Boolean = false

    override suspend fun invoke(uri: Uri) {
        invoked = true
    }
}
