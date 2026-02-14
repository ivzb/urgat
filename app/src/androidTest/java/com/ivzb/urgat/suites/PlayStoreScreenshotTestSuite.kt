package com.ivzb.urgat.suites

import com.ivzb.urgat.annotations.ExcludeFromUiTests
import com.ivzb.urgat.ui.addEditTodo.AddEditTodoScreenshotTest
import com.ivzb.urgat.ui.finance.FinanceScreenshotTest
import com.ivzb.urgat.ui.library.LibraryScreenshotTest
import com.ivzb.urgat.ui.wellbeing.WellbeingScreenshotTest
import org.junit.experimental.categories.Categories
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExcludeFromUiTests
@RunWith(Categories::class)
@Categories.IncludeCategory(PlayStoreScreenshotTest::class)
@Suite.SuiteClasses(
    LibraryScreenshotTest::class,
    AddEditTodoScreenshotTest::class,
    FinanceScreenshotTest::class,
    WellbeingScreenshotTest::class,
)
class PlayStoreScreenshotTestSuite