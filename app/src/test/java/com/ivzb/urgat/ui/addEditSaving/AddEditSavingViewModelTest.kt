package com.ivzb.urgat.ui.addEditSaving

import com.ivzb.urgat.R
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.saving.FakeCreateSavingUseCase
import com.ivzb.urgat.domain.saving.FakeDeleteSavingUseCase
import com.ivzb.urgat.domain.saving.FakeGetSavingUseCase
import com.ivzb.urgat.domain.saving.FakeUpdateSavingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditSavingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetSavingUseCase

    private lateinit var createUseCase: FakeCreateSavingUseCase

    private lateinit var updateUseCase: FakeUpdateSavingUseCase

    private lateinit var deleteUseCase: FakeDeleteSavingUseCase

    private lateinit var viewModel: AddEditSavingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetSavingUseCase()
        createUseCase = FakeCreateSavingUseCase()
        updateUseCase = FakeUpdateSavingUseCase()
        deleteUseCase = FakeDeleteSavingUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load saving`() = runTest {
        Assert.assertNull(getUseCase.calledWithId)
        Assert.assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads saving`() = runTest {
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "Emergency fund",
            amount = BigDecimal("500"),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = saving

        viewModel = createViewModel(id = savingId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        Assert.assertEquals("Emergency fund", state.name)
        Assert.assertEquals("500", state.amount)
        Assert.assertEquals(123L, state.date)
        Assert.assertEquals(false, state.isLoading)
        Assert.assertEquals(true, state.withDelete)
        Assert.assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateName updates uiState`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.UpdateName("Emergency fund"))
        Assert.assertEquals("Emergency fund", viewModel.uiState.value.name)
    }

    @Test
    fun `saveSaving with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)
        Assert.assertEquals(R.string.empty_saving_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveSaving with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.UpdateName(" "))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount(" "))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        Assert.assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        Assert.assertEquals(R.string.empty_saving_message, uiState.userMessage)
        Assert.assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveSaving creates new saving`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.UpdateName("Emergency fund"))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount("500"))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (name, amount) = createUseCase.created
        Assert.assertEquals("Emergency fund", name)
        Assert.assertEquals(BigDecimal("500"), amount)
        Assert.assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveSaving creates new saving trimmed`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.UpdateName(" Emergency fund "))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount(" 500 "))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (name, amount) = createUseCase.created
        Assert.assertEquals("Emergency fund", name)
        Assert.assertEquals(BigDecimal("500"), amount)
        Assert.assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveSaving updates existing saving`() = runTest {
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "Emergency fund",
            amount = BigDecimal("500"),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = saving

        viewModel = createViewModel(savingId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        Assert.assertEquals(saving.name, uiState.name)
        Assert.assertEquals(saving.amount.toString(), uiState.amount)
        Assert.assertEquals(saving.date, uiState.date)
        Assert.assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditSavingIntent.UpdateName("Edit"))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount("35"))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (id, name, amount) = updateUseCase.updated
        Assert.assertEquals(savingId, id)
        Assert.assertEquals("Edit", name)
        Assert.assertEquals(BigDecimal("35"), amount)
        Assert.assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveSaving updates existing saving trimmed`() = runTest {
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "Emergency fund",
            amount = BigDecimal("500"),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = saving

        viewModel = createViewModel(savingId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        Assert.assertEquals(saving.name, uiState.name)
        Assert.assertEquals(saving.amount.toString(), uiState.amount)
        Assert.assertEquals(saving.date, uiState.date)
        Assert.assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditSavingIntent.UpdateName(" Edit "))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount(" 350 "))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (id, name, amount) = updateUseCase.updated
        Assert.assertEquals(savingId, id)
        Assert.assertEquals("Edit", name)
        Assert.assertEquals(BigDecimal("350"), amount)
        Assert.assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteSaving deletes existing saving`() = runTest {
        val savingId = "123"

        viewModel = createViewModel(savingId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditSavingIntent.DeleteSaving)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        Assert.assertEquals(savingId, id)
    }

    @Test
    fun `deleteSaving deletes non-existing saving`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditSavingIntent.DeleteSaving)

            val uiState = viewModel.uiState.value
            Assert.assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)
        Assert.assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditSavingIntent.UserMessageShown)
        Assert.assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel(id: String?): AddEditSavingViewModel {
        val route = AddEditSavingRoute(id, if (id == null) R.string.add_saving else R.string.edit_saving)

        return AddEditSavingViewModel(
            route,
            getUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }
}