package com.ivzb.urgat.ui.exportData

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object ExportDataRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.export_data

}