package com.ivzb.urgat.domain.export

import android.net.Uri

class FakeExportUseCase : ExportUseCase {

    var invoked: Boolean = false

    override suspend fun invoke(uri: Uri) {
        invoked = true
    }
}
