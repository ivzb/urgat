package com.ivzb.urgat.domain.importData

import android.net.Uri

class FakeImportUseCase : ImportDataUseCase {

    var invoked: Boolean = false

    override suspend fun invoke(uri: Uri) {
        invoked = true
    }
}
