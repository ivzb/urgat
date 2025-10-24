package com.ivzb.urgat.ui.components

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DatePicker(
    timestamp: Long,
    onTimestampSelected: (Long) -> Unit,
    content: @Composable (String, DatePickerDialog) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    val selectedDate = remember(timestamp) {
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
    val formattedDate = selectedDate.format(dateFormat)
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
                val pickedMillis = pickedDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                onTimestampSelected(pickedMillis)
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        )
    }

    content(formattedDate, datePickerDialog)
}
