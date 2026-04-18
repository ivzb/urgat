package com.ivzb.urgat.ui.carMaintenance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.util.toLocalDate


@Composable
fun CarMaintenanceItem(
    carMaintenance: CarMaintenance,
    onCarMaintenanceClick: (CarMaintenance) -> Unit,
) {
    val date = remember { carMaintenance.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = carMaintenance.carName,
        subTextRight = carMaintenance.summary,

        onClick = { onCarMaintenanceClick(carMaintenance) }
    )
}