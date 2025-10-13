package com.ivzb.urgat.ui.expenses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.ExpensesDestination
import com.ivzb.urgat.ui.components.MenuScaffold
import com.ivzb.urgat.ui.theme.UrgatTheme
import kotlinx.coroutines.launch

@Composable
fun ExpensesScreen(
    onAddExpense: () -> Unit,

    navController: NavHostController,

    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = hiltViewModel()
) {

    MenuScaffold(
        currentDestination = ExpensesDestination,
        onSearchQueryChanged = {
            TODO("update view model")
        },
        navController = navController,
        modifier = modifier
    ) {

        val items by viewModel.uiState.collectAsStateWithLifecycle()

        if (items is ExpensesUiState.Success) {
            ExpensesScreen(
                expenses = (items as ExpensesUiState.Success).data,
                onAddExpense = onAddExpense,
                modifier = modifier
            )
        }
    }
}

@Composable
internal fun ExpensesScreen(
    expenses: List<String>,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {

    val lazyListState = rememberLazyListState()

    // The coroutine scope for event handlers calling suspend functions.
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            ExpensesFloatingActionButton(
                extended = lazyListState.isScrollingUp(),
                onClick = {
                    coroutineScope.launch {
                        onAddExpense()
                    }
                }
            )
        }
    ) { padding ->

        Box(modifier = modifier) {
            LazyColumn(
                contentPadding = WindowInsets(16.dp, 32.dp, 16.dp,
                    padding.calculateBottomPadding() + 32.dp
                ).asPaddingValues(),
                state = lazyListState
            ) {
                item { Header(title = stringResource(R.string.expenses)) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(expenses) { expense ->
                    ExpenseRow(expense)
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun ExpensesFloatingActionButton(
    extended: Boolean,
    onClick: () -> Unit
) {
    // Use `FloatingActionButton` rather than `ExtendedFloatingActionButton` for full control on
    // how it should animate.
    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            // Toggle the visibility of the content with animation.
            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(R.string.add),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}

/**
 * Returns whether the lazy list is currently scrolling up.
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Composable
private fun Header(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
private fun ExpenseRow(expense: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = expense,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    UrgatTheme {
        ExpensesScreen(listOf("Compose", "Room", "Kotlin"), onAddExpense = {})
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    UrgatTheme {
        ExpensesScreen(listOf("Compose", "Room", "Kotlin"), onAddExpense = {})
    }
}
