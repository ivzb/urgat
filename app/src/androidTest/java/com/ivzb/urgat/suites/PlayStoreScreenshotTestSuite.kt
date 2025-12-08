package com.ivzb.urgat.suites

import com.ivzb.urgat.ui.addEditTodo.AddEditTodoScreenTest
import com.ivzb.urgat.ui.expenses.ExpensesScreenshotTest
import com.ivzb.urgat.ui.finance.FinanceScreenshotTest
import com.ivzb.urgat.ui.investments.InvestmentsScreenshotTest
import com.ivzb.urgat.ui.library.LibraryScreenshotTest
import com.ivzb.urgat.ui.notes.NotesScreenshotTest
import com.ivzb.urgat.ui.todo.TodoScreenshotTest
import com.ivzb.urgat.ui.wellbeing.WellbeingScreenshotTest
import org.junit.experimental.categories.Categories
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Categories::class)
@Categories.IncludeCategory(PlayStoreScreenshotTest::class)
@Suite.SuiteClasses(
    LibraryScreenshotTest::class,
    TodoScreenshotTest::class,
    AddEditTodoScreenTest::class,
    NotesScreenshotTest::class,
    FinanceScreenshotTest::class,
    ExpensesScreenshotTest::class,
    InvestmentsScreenshotTest::class,
    WellbeingScreenshotTest::class,
)
class PlayStoreScreenshotTestSuite