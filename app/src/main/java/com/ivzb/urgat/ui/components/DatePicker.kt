package com.ivzb.urgat.ui.components

import android.app.DatePickerDialog
import android.view.ContextThemeWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate
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
    val selectedDate = remember(timestamp) {
        timestamp.toLocalDate()
    }
    val formattedDate = selectedDate.format()
    val isDarkTheme = isSystemInDarkTheme()
    val themedContext = remember(context, isDarkTheme) {
        ContextThemeWrapper(context, if (isDarkTheme) {
            android.R.style.ThemeOverlay_Material_Dark
        } else {
            android.R.style.ThemeOverlay_Material_Light
        })
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            themedContext,
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
