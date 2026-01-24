package com.ivzb.urgat.ui.savings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.theme.savingColor
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate

@Composable
fun SavingItem(
    saving: Saving,
    onSavingClick: (Saving) -> Unit
) {
    val date = remember { saving.date.toLocalDate() }

    ListItem(
        textLeft = date.dayOfMonth.toString(),
        subTextLeft = date.month.name.take(3),

        textRight = saving.name,
        subTextRight = saving.amount.format(),

        subTextRightFontWeight = FontWeight.Bold,
        subTextRightColor = savingColor(saving.amount),

        onClick = { onSavingClick(saving) }
    )
}