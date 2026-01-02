package com.ivzb.urgat.ui.addEditWeight

import com.ivzb.urgat.R
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.weight.FakeCreateWeightUseCase
import com.ivzb.urgat.domain.weight.FakeDeleteWeightUseCase
import com.ivzb.urgat.domain.weight.FakeGetWeightUseCase
import com.ivzb.urgat.domain.weight.FakeUpdateWeightUseCase
import io.mockk.InternalPlatformDsl.toStr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditWeightViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetWeightUseCase

    private lateinit var createUseCase: FakeCreateWeightUseCase

    private lateinit var updateUseCase: FakeUpdateWeightUseCase

    private lateinit var deleteUseCase: FakeDeleteWeightUseCase

    private lateinit var viewModel: AddEditWeightViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetWeightUseCase()
        createUseCase = FakeCreateWeightUseCase()
        updateUseCase = FakeUpdateWeightUseCase()
        deleteUseCase = FakeDeleteWeightUseCase()        
        
        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load weight`() = runTest {
        assertNull(getUseCase.calledWithId)
        
        val uiState = viewModel.uiState.value
        assertEquals("", uiState.value)
        assertFalse(uiState.isLoading)
        assertNull(uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `init with id loads weight`() = runTest {
        val weightId = "123"
        val weight = Weight(
            id = weightId,
            value = "80.5".toBigDecimal(),
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = weight

        val viewModel = createViewModel(id = weightId)

        advanceUntilIdle()
        
        assertEquals("123", getUseCase.calledWithId)

        val state = viewModel.uiState.value
        assertEquals("80.5", state.value)
        assertEquals(1L, state.date)
        assertEquals(false, state.isLoading)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateValue updates uiState`() = runTest {
        viewModel.onIntent(AddEditWeightIntent.UpdateValue("81.5"))
        assertEquals("81.5", viewModel.uiState.value.value)
    }

    @Test
    fun `saveWeight with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditWeightIntent.SaveWeight)
        assertEquals(R.string.empty_weight_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveWeight creates new weight`() = runTest {
        viewModel.onIntent(AddEditWeightIntent.UpdateValue("80"))
        viewModel.onIntent(AddEditWeightIntent.UpdateDate(1764523811))
        viewModel.onIntent(AddEditWeightIntent.SaveWeight)
        
        advanceUntilIdle()

        val (value, date) = createUseCase.created
        assertEquals(BigDecimal("80"), value)
        assertEquals(1764523811L, date)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveWeight updates existing weight`() = runTest {
        val weightId = "123"
        val weight = Weight(
            id = weightId,
            value = "80.5".toBigDecimal(),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = weight

        viewModel = createViewModel(weightId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(weight.value.toStr(), uiState.value)
        assertEquals(weight.date, uiState.date)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditWeightIntent.UpdateValue("81.3"))
        viewModel.onIntent(AddEditWeightIntent.UpdateDate(1764523811L))
        viewModel.onIntent(AddEditWeightIntent.SaveWeight)

        advanceUntilIdle()

        val (id, value, date) = updateUseCase.updated
        assertEquals(weightId, id)
        assertEquals("81.3".toBigDecimal(), value)
        assertEquals(1764523811L, date)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteWeight deletes existing weight`() = runTest {
        val weightId = "123"

        viewModel = createViewModel(weightId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditWeightIntent.DeleteWeight)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(weightId, id)
    }

    @Test
    fun `deleteWeight deletes non-existing weight`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditWeightIntent.DeleteWeight)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditWeightIntent.SaveWeight)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditWeightIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel(id: String?): AddEditWeightViewModel {
        val route = AddEditWeightRoute(id, if (id == null) R.string.add_weight else R.string.edit_weight)
        
        return AddEditWeightViewModel(
            route,
            getUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }
}