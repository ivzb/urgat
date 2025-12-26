package com.ivzb.urgat.navigation.drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Settings
import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.exportData.ExportDataRoute
import com.ivzb.urgat.ui.importData.ImportDataRoute
import com.ivzb.urgat.ui.sample.SampleRoute

val URGAT_NAVIGATION_DRAWER = listOfNotNull(
    UrgatNavigationDrawerSection(
        titleResId = R.string.debug,
        items = listOf(
            UrgatNavigationDrawerItem(
                labelResId = R.string.sample_data,
                iconVector = Icons.Outlined.Settings,
                route = SampleRoute,
            )
        )
    ).takeIf { BuildConfig.DEBUG },

    UrgatNavigationDrawerSection(
        titleResId = R.string.data,
        items = listOf(
            UrgatNavigationDrawerItem(
                labelResId = R.string.export_data,
                iconVector = Icons.Outlined.ImportExport,
                route = ExportDataRoute,
            ),
            UrgatNavigationDrawerItem(
                labelResId = R.string.import_data,
                iconVector = Icons.Outlined.ImportExport,
                route = ImportDataRoute,
            ),
        )
    ),
)