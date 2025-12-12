package com.ivzb.urgat.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun ExpandableFab(
    actions: List<FabAction>
) {
    var isExpanded by remember { mutableStateOf(false) }
    val onToggle = { isExpanded = !isExpanded }

    ExpandableFab(
        actions,
        isExpanded = isExpanded,
        onToggle = onToggle
    )
}

@Composable
fun ExpandableFab(
    actions: List<FabAction>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        label = "RotationAnimation"
    )

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

        if (isExpanded) {
            actions.reversed().forEach { action ->
                FloatingActionButtonContent(
                    extended = true,
                    onClick = {
                        action.onClick()
                        onToggle()
                    }
                )

                Spacer(modifier = Modifier
                    .height(10.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = { onToggle() },
            containerColor = MaterialTheme.colorScheme.primary
        ) {

            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Create New",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Preview
@Composable
private fun ExpandableFabCollapsedPreview() {
    UrgatTheme {
        ExpandableFab(
            actions = listOf(
                FabAction(
                    icon = Icons.Filled.Done,
                    labelResId = R.string.add_todo,
                    onClick = {}
                ),
                FabAction(
                    icon = Icons.Filled.Person,
                    labelResId = R.string.add_note,
                    onClick = {}
                )
            ),
            isExpanded = false,
            onToggle = {}
        )
    }
}

@Preview
@Composable
private fun ExpandableFabExpandedPreview() {
    UrgatTheme {
        ExpandableFab(
            actions = listOf(
                FabAction(
                    icon = Icons.Filled.Done,
                    labelResId = R.string.add_todo,
                    onClick = {}
                ),
                FabAction(
                    icon = Icons.Filled.Person,
                    labelResId = R.string.add_note,
                    onClick = {}
                )
            ),
            isExpanded = true,
            onToggle = {}
        )
    }
}
