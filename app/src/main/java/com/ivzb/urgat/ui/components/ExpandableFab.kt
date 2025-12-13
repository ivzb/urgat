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
import com.ivzb.urgat.ui.library.LibraryFabActionId
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun <T> ExpandableFab(
    fabActions: List<FabAction<T>>,
    onFabActionClick: (T) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val onToggle = { isExpanded = !isExpanded }

    ExpandableFab(
        fabActions = fabActions,
        onFabActionClick = onFabActionClick,
        isExpanded = isExpanded,
        onToggle = onToggle
    )
}

@Composable
fun <T> ExpandableFab(
    fabActions: List<FabAction<T>>,
    onFabActionClick: (T) -> Unit,
    isExpanded: Boolean,
    onToggle: () -> Unit,
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
            fabActions.reversed().forEach { fabAction ->
                FloatingActionButtonContent(
                    extended = true,
                    icon = fabAction.icon,
                    labelResId = fabAction.labelResId,
                    onClick = {
                        onFabActionClick(fabAction.id)
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
            fabActions = listOf(
                FabAction(
                    id = LibraryFabActionId.Todo,
                    icon = Icons.Filled.Done,
                    labelResId = R.string.add_todo,
                ),
                FabAction(
                    id = LibraryFabActionId.Note,
                    icon = Icons.Filled.Person,
                    labelResId = R.string.add_note,
                )
            ),
            onFabActionClick = {},
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
            fabActions = listOf(
                FabAction(
                    id = LibraryFabActionId.Todo,
                    icon = Icons.Filled.Done,
                    labelResId = R.string.add_todo,
                ),
                FabAction(
                    id = LibraryFabActionId.Note,
                    icon = Icons.Filled.Person,
                    labelResId = R.string.add_note,
                )
            ),
            onFabActionClick = {},
            isExpanded = true,
            onToggle = {}
        )
    }
}
