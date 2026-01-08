package com.ivzb.urgat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import com.ivzb.urgat.R

@Composable
fun CheckboxWithLabel(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    label: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = value,
                role = Role.Checkbox,
                onValueChange = onValueChange,
            )
            .padding(dimensionResource(R.dimen.checkbox_padding)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.checkbox_padding)),
    ) {

        Checkbox(
            checked = value,
            onCheckedChange = null
        )

        Text(
            text = label,
        )
    }
}