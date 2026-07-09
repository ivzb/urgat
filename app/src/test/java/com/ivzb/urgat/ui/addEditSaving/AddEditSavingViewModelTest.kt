package com.ivzb.urgat.ui.addEditSaving

import com.ivzb.urgat.R
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.saving.FakeCreateSavingUseCase
import com.ivzb.urgat.domain.saving.FakeDeleteSavingUseCase
import com.ivzb.urgat.domain.saving.FakeGetSavingUseCase
import com.ivzb.urgat.domain.saving.FakeGetLastUsedSavingCurrencyUseCase
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditSavingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetSavingUseCase

    private lateinit var getLastUsedCurrencyUseCase: FakeGetLastUsedSavingCurrencyUseCase

    private lateinit var createUseCase: FakeCreateSavingUseCase

    private lateinit var updateUseCase: FakeUpdateSavingUseCase

    private lateinit var deleteUseCase: FakeDeleteSavingUseCase

    private lateinit var viewModel: AddEditSavingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetSavingUseCase()
        getLastUsedCurrencyUseCase = FakeGetLastUsedSavingCurrencyUseCase()
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
            currency = "EUR",
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
        Assert.assertEquals("EUR", state.currency)
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
    fun `updateCurrency updates uiState`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.UpdateCurrency("USD"))
        Assert.assertEquals("USD", viewModel.uiState.value.currency)
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
        viewModel.onIntent(AddEditSavingIntent.UpdateCurrency(" "))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        Assert.assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        Assert.assertEquals(R.string.empty_saving_message, uiState.userMessage)
        Assert.assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveSaving with empty currency shows message`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.UpdateName("Emergency fund"))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount("500"))
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
        viewModel.onIntent(AddEditSavingIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (name, amount, currency) = createUseCase.created
        Assert.assertEquals("Emergency fund", name)
        Assert.assertEquals(BigDecimal("500"), amount)
        Assert.assertEquals("EUR", currency)
        Assert.assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveSaving creates new saving trimmed`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.UpdateName(" Emergency fund "))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount(" 500 "))
        viewModel.onIntent(AddEditSavingIntent.UpdateCurrency(" EUR "))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (name, amount, currency) = createUseCase.created
        Assert.assertEquals("Emergency fund", name)
        Assert.assertEquals(BigDecimal("500"), amount)
        Assert.assertEquals("EUR", currency)
        Assert.assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveSaving updates existing saving`() = runTest {
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "Emergency fund",
            amount = BigDecimal("500"),
            currency = "EUR",
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
        Assert.assertEquals(saving.currency, uiState.currency)
        Assert.assertEquals(saving.date, uiState.date)
        Assert.assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditSavingIntent.UpdateName("Edit"))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount("35"))
        viewModel.onIntent(AddEditSavingIntent.UpdateCurrency("USD"))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (id, name, amount, currency) = updateUseCase.updated
        Assert.assertEquals(savingId, id)
        Assert.assertEquals("Edit", name)
        Assert.assertEquals(BigDecimal("35"), amount)
        Assert.assertEquals("USD", currency)
        Assert.assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveSaving updates existing saving trimmed`() = runTest {
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "Emergency fund",
            amount = BigDecimal("500"),
            currency = "EUR",
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
        Assert.assertEquals(saving.currency, uiState.currency)
        Assert.assertEquals(saving.date, uiState.date)
        Assert.assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditSavingIntent.UpdateName(" Edit "))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount(" 350 "))
        viewModel.onIntent(AddEditSavingIntent.UpdateCurrency(" USD "))
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val (id, name, amount, currency) = updateUseCase.updated
        Assert.assertEquals(savingId, id)
        Assert.assertEquals("Edit", name)
        Assert.assertEquals(BigDecimal("350"), amount)
        Assert.assertEquals("USD", currency)
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
    fun `duplicateSaving resets state to create mode with current data`() = runTest {
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "SNP 500",
            amount = BigDecimal("1500"),
            currency = "EUR",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = saving

        viewModel = createViewModel(savingId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditSavingIntent.DuplicateSaving)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.add_saving, state.screenTitleResId)
        assertEquals("SNP 500", state.name)
        assertEquals("1500", state.amount)
        assertEquals("EUR", state.currency)
        assertTrue(state.date > 123L)
        Assert.assertFalse(state.withDelete)
        Assert.assertFalse(state.withDuplicate)
        Assert.assertFalse(state.shouldNavigateBack)
    }

    @Test
    fun `duplicateSaving then save creates new saving`() = runTest {
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "SNP 500",
            amount = BigDecimal("1500"),
            currency = "EUR",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = saving

        viewModel = createViewModel(savingId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditSavingIntent.DuplicateSaving)

        advanceUntilIdle()

        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isNotEmpty())
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateSaving with modified fields then save creates new saving`() = runTest {
        val savingId = "456"
        val saving = Saving(
            id = savingId,
            name = "SNP 500",
            amount = BigDecimal("1500"),
            currency = "EUR",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = saving

        viewModel = createViewModel(savingId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditSavingIntent.UpdateName("NVDA"))
        viewModel.onIntent(AddEditSavingIntent.UpdateAmount("3000"))
        viewModel.onIntent(AddEditSavingIntent.UpdateCurrency("USD"))
        viewModel.onIntent(AddEditSavingIntent.DuplicateSaving)

        advanceUntilIdle()

        viewModel.onIntent(AddEditSavingIntent.SaveSaving)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.add_saving, state.screenTitleResId)
        assertTrue(createUseCase.created.isNotEmpty())
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(state.shouldNavigateBack)
    }

    @Test
    fun `duplicateSaving without id shows error`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditSavingIntent.DuplicateSaving)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditSavingIntent.SaveSaving)
        Assert.assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditSavingIntent.UserMessageShown)
        Assert.assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun `init without id autofills last used currency`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = "EUR"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("EUR", viewModel.uiState.value.currency)
    }

    @Test
    fun `init without id and no last currency keeps empty currency`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = null

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.currency)
    }

    @Test
    fun `init with id does not autofill last used currency`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = "USD"
        val savingId = "123"
        val saving = Saving(
            id = savingId,
            name = "Emergency fund",
            amount = BigDecimal("500"),
            currency = "EUR",
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = saving

        viewModel = createViewModel(id = savingId)

        advanceUntilIdle()

        assertEquals("EUR", viewModel.uiState.value.currency)
    }

    private fun createViewModel(id: String?): AddEditSavingViewModel {
        val route = AddEditSavingRoute(id, if (id == null) R.string.add_saving else R.string.edit_saving)

        return AddEditSavingViewModel(
            route,
            getUseCase,
            getLastUsedCurrencyUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }
}