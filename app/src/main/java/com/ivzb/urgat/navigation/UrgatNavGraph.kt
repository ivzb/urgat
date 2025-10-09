package com.ivzb.urgat.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.ivzb.urgat.ui.expenses.ExpensesScreen
import com.ivzb.urgat.ui.notes.NotesScreen
import com.ivzb.urgat.ui.overview.OverviewScreen
import com.ivzb.urgat.ui.theme.UrgatTheme
import com.ivzb.urgat.ui.todo.TodoScreen
import com.ivzb.urgat.ui.weather.WeatherScreen

@Composable
fun UrgatNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navActions: UrgatNavActions = remember(navController) {
        UrgatNavActions(navController)
    }
) {
    UrgatTheme {
        Scaffold(
            bottomBar = {
                BottomBarContent(navController)
            },
            floatingActionButton = {
                FloatingActionButtonContent(navController, navActions)
            }
        ) { innerPadding ->

            NavHostContent(
                navController,
                navActions,
                modifier = modifier.padding(
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        top = 0.dp, // Ignore top padding because top bar is missing
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                )
            )
        }
    }
}

@Composable
private fun BottomBarContent(
    navController: NavHostController,
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentTabScreen = bottomNavigationScreens.find { it.route == currentDestination?.route }

    if (currentTabScreen != null) {
        NavigationBar {
            bottomNavigationScreens.forEach { item ->
                NavigationBarItem(
                    selected = currentTabScreen == item,
                    onClick = {
                        navController.navigate(item.route)
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
                            color = if (currentTabScreen == item) {
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
    navController: NavHostController,
    navActions: UrgatNavActions
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentTabScreen = bottomNavigationScreens.find { it.route == currentDestination?.route }

    if (currentTabScreen == TodoDestination) {
        FloatingActionButton(onClick = {
            navActions.navigateToAddEditTodo(R.string.add_todo, null)
        }) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_todo))
        }
    }
}

@Composable
private fun NavHostContent(
    navController: NavHostController,
    navActions: UrgatNavActions,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = OverviewDestination.route,
        modifier = modifier
    ) {
        composable(OverviewDestination.route) {
            OverviewScreen()
        }

        composable(TodoDestination.route) {
            TodoScreen(
                onTodoClick = {
                    navActions.navigateToAddEditTodo(R.string.edit_todo, it.id)
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

        composable(AddEditTodoDestination.route, arguments = listOf(
            navArgument(AddEditTodoDestination.Args.TITLE_ARG) { type = NavType.IntType },
            navArgument(AddEditTodoDestination.Args.TODO_ID_ARG) { type = NavType.StringType; nullable = true },
        )) { entry ->
            val title = entry.arguments?.getInt(AddEditTodoDestination.Args.TITLE_ARG) ?: 0
            val todoId = entry.arguments?.getString(AddEditTodoDestination.Args.TODO_ID_ARG)

            AddEditTodoScreen(
                screenTitle = title,
                onTodoUpdate = {
                    navActions.navigateToTodo()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AddExpenseDestination.route) {
            AddExpenseScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String, popInclusive: Boolean = false) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
            inclusive = popInclusive
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
