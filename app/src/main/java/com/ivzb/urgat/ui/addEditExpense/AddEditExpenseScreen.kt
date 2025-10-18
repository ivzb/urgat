package com.ivzb.urgat.ui.addEditExpense

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddEditExpenseScreen(
    @StringRes screenTitle: Int,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    Text("add/edit expense screen")
}