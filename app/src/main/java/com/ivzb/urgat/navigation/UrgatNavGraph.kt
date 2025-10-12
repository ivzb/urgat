package com.ivzb.urgat.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoScreen
import com.ivzb.urgat.ui.addExpense.AddExpenseScreen
import com.ivzb.urgat.ui.components.SearchableTopBar
import com.ivzb.urgat.ui.expenses.ExpensesScreen
import com.ivzb.urgat.ui.notes.NotesScreen
import com.ivzb.urgat.ui.overview.OverviewScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todo.TodoScreen
import com.ivzb.urgat.ui.weather.WeatherScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgatNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navActions: UrgatNavActions = remember(navController) {
        UrgatNavActions(navController)
    },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentTabScreen = bottomNavigationScreens.find { it.route == currentDestination?.route }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBarContent(
                currentDestination = currentTabScreen,
                onSearchQueryChanged = {
                    searchQuery = it
                }
            )
        },
        bottomBar = {
            BottomBarContent(
                currentDestination = currentTabScreen,
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButtonContent(
                currentTabScreen,
                onAddTodo = {
                    navActions.navigateToAddTodo()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->

        NavHostContent(
            navController,
            navActions,
            onShowSnackbar = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            },
            modifier = modifier
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarContent(
    currentDestination: UrgatMenuDestination?,
    onSearchQueryChanged: (String) -> Unit
) {
    if (currentDestination != null) {
        var isSearching by rememberSaveable { mutableStateOf(false) }
        var searchQuery by rememberSaveable { mutableStateOf("") }

        SearchableTopBar(
            isSearching = isSearching,
            onIsSearchingChanged = { isSearching = it },
            searchQuery = searchQuery,
            onSearchQueryChanged = {
                searchQuery = it
                onSearchQueryChanged(it)
            },
            titleResId = currentDestination.label,
            actionsContent = {
                if (currentDestination == OverviewDestination || currentDestination == TodoDestination) {
                    TopAppBarDropdownMenu(
                        iconContent = {
                            Icon(Icons.Filled.MoreVert, stringResource(id = R.string.more))
                        }
                    ) { closeMenu ->
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.clear_completed)) },
                            onClick = {
                                TODO("clear completed todos")
                                closeMenu()
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun TopAppBarDropdownMenu(
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            iconContent()
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            content { expanded = !expanded }
        }
    }
}

@Composable
private fun BottomBarContent(
    currentDestination: UrgatMenuDestination?,
    onItemClick: (UrgatMenuDestination) -> Unit
) {
    if (currentDestination != null) {
        NavigationBar {
            bottomNavigationScreens.forEach { item ->
                NavigationBarItem(
                    selected = currentDestination == item,
                    onClick = {
                        onItemClick(item)
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.label)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.label),
                            color = if (currentDestination == item) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.surface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun FloatingActionButtonContent(
    currentDestination: UrgatMenuDestination?,
    onAddTodo: () -> Unit
) {
    if (currentDestination == TodoDestination) {
        FloatingActionButton(onClick = {
            onAddTodo()
        }) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_todo))
        }
    }
}

@Composable
private fun NavHostContent(
    navController: NavHostController,
    navActions: UrgatNavActions,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = OverviewDestination.route,
        modifier = modifier
    ) {
        composable(OverviewDestination.route) {
            OverviewScreen(
                onAddTodo = { navActions.navigateToAddTodo() },
                onTodoClick = { navActions.navigateToEditTodo(it.id) },
                onAddExpense = {},
                onExpenseClick = {},
                onAddNote = {},
                onNoteClick = {},
                modifier = modifier
            )
        }

        composable(TodoDestination.route) {
            TodoScreen(
                onTodoClick = {
                    navActions.navigateToEditTodo(it.id)
                }
            )
        }

        composable(ExpensesDestination.route) {
            ExpensesScreen(
                onAddExpense = {
                    navActions.navigateToAddEditExpense()
                },
                modifier = Modifier.padding(16.dp)
            )
        }

        composable(NotesDestination.route) {
            NotesScreen()
        }

        composable(WeatherDestination.route) {
            WeatherScreen()
        }

        composable(
            AddEditTodoDestination.route, arguments = listOf(
                navArgument(AddEditTodoDestination.Args.ARG_TITLE) { type = NavType.IntType },
                navArgument(AddEditTodoDestination.Args.ARG_TODO_ID) {
                    type = NavType.StringType; nullable = true
                },
            )
        ) { entry ->
            val title = entry.arguments?.getInt(AddEditTodoDestination.Args.ARG_TITLE) ?: 0
            val todoId = entry.arguments?.getString(AddEditTodoDestination.Args.ARG_TODO_ID)

            AddEditTodoScreen(
                screenTitle = title,
                onBack = { navController.popBackStack() },
                onShowSnackbar = onShowSnackbar
            )
        }

        composable(AddExpenseDestination.route) {
            AddExpenseScreen()
        }
    }
}

@Preview
@Composable
fun UrgatNavGraphPreview() {
    UrgatTheme {
        UrgatNavGraph()
    }
}

@Preview
@Composable
fun TopAppBarContentOverviewDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            currentDestination = OverviewDestination,
            onSearchQueryChanged = {}
        )
    }
}

@Preview
@Composable
fun TopAppBarContentTodoDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            currentDestination = TodoDestination,
            onSearchQueryChanged = {}
        )
    }
}

@Preview
@Composable
fun TopAppBarContentEmptyDestinationPreview() {
    UrgatTheme {
        TopAppBarContent(
            currentDestination = null,
            onSearchQueryChanged = {}
        )
    }
}

@Preview
@Composable
fun BottomBarContentOverviewDestinationPreview() {
    UrgatTheme {
        BottomBarContent(
            currentDestination = OverviewDestination,
            onItemClick = {}
        )
    }
}

@Preview
@Composable
fun BottomBarContentNoDestinationPreview() {
    UrgatTheme {
        BottomBarContent(
            currentDestination = null,
            onItemClick = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonContentOverviewDestinationPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            currentDestination = OverviewDestination,
            onAddTodo = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonContentTodoDestinationPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            currentDestination = TodoDestination,
            onAddTodo = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonContentNoDestinationPreview() {
    UrgatTheme {
        FloatingActionButtonContent(
            currentDestination = null,
            onAddTodo = {}
        )
    }
}
