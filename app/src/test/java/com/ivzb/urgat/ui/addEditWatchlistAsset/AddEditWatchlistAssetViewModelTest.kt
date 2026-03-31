package com.ivzb.urgat.ui.addEditWatchlistAsset

import com.ivzb.urgat.R
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.domain.watchlist.FakeCreateWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.FakeDeleteWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.FakeGetWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.FakeUpdateWatchlistAssetUseCase
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
class AddEditWatchlistAssetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetWatchlistAssetUseCase

    private lateinit var createUseCase: FakeCreateWatchlistAssetUseCase

    private lateinit var updateUseCase: FakeUpdateWatchlistAssetUseCase

    private lateinit var deleteUseCase: FakeDeleteWatchlistAssetUseCase

    private lateinit var viewModel: AddEditWatchlistAssetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetWatchlistAssetUseCase()
        createUseCase = FakeCreateWatchlistAssetUseCase()
        updateUseCase = FakeUpdateWatchlistAssetUseCase()
        deleteUseCase = FakeDeleteWatchlistAssetUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load watchlist asset`() = runTest {
        assertNull(getUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads watchlist asset`() = runTest {
        val assetId = "123"
        val asset = WatchlistAsset(
            id = assetId,
            name = "AAPL",
            description = "Apple Inc. (AAPL)",
            marketPrice = BigDecimal("246.63"),
            currency = "USD",
            previousClosePrice = BigDecimal("244.46"),
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = asset

        viewModel = createViewModel(id = assetId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("AAPL", state.name)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateName updates uiState`() = runTest {
        viewModel.onIntent(AddEditWatchlistAssetIntent.UpdateName("AAPL"))
        assertEquals("AAPL", viewModel.uiState.value.name)
    }

    @Test
    fun `saveWatchlistAsset with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)
        assertEquals(R.string.empty_watchlist_asset_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveWatchlistAsset with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditWatchlistAssetIntent.UpdateName(" "))
        viewModel.onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        assertEquals(R.string.empty_watchlist_asset_message, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveWatchlistAsset creates new watchlist asset`() = runTest {
        viewModel.onIntent(AddEditWatchlistAssetIntent.UpdateName("AAPL"))
        viewModel.onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)

        advanceUntilIdle()

        val expected = listOf("AAPL")
        val actual = createUseCase.created
        assertEquals(expected, actual)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveWatchlistAsset creates new watchlist asset trimmed`() = runTest {
        viewModel.onIntent(AddEditWatchlistAssetIntent.UpdateName(" AAPL "))
        viewModel.onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)

        advanceUntilIdle()

        val expected = listOf("AAPL")
        val actual = createUseCase.created
        assertEquals(expected, actual)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveWatchlistAsset updates existing watchlist asset`() = runTest {
        val assetId = "123"
        val asset = WatchlistAsset(
            id = assetId,
            name = "AAPL",
            description = "Apple Inc. (AAPL)",
            marketPrice = BigDecimal("246.63"),
            currency = "USD",
            previousClosePrice = BigDecimal("244.46"),
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = asset

        viewModel = createViewModel(id = assetId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(asset.name, uiState.name)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditWatchlistAssetIntent.UpdateName("GOOGL"))
        viewModel.onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)

        advanceUntilIdle()

        val expected = listOf(assetId, "GOOGL")
        val actual = updateUseCase.updated
        assertEquals(expected, actual)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveWatchlistAsset updates existing watchlist asset trimmed`() = runTest {
        val assetId = "123"
        val asset = WatchlistAsset(
            id = assetId,
            name = "AAPL",
            description = "Apple Inc. (AAPL)",
            marketPrice = BigDecimal("246.63"),
            currency = "USD",
            previousClosePrice = BigDecimal("244.46"),
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = asset

        viewModel = createViewModel(id = assetId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(asset.name, uiState.name)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditWatchlistAssetIntent.UpdateName(" GOOGL "))
        viewModel.onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)

        advanceUntilIdle()

        val expected = listOf(assetId, "GOOGL")
        val actual = updateUseCase.updated
        assertEquals(expected, actual)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteWatchlistAsset deletes existing watchlist asset`() = runTest {
        val assetId = "123"

        viewModel = createViewModel(id = assetId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditWatchlistAssetIntent.DeleteWatchlistAsset)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(assetId, id)
    }

    @Test
    fun `deleteWatchlistAsset deletes non-existing watchlist asset`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditWatchlistAssetIntent.DeleteWatchlistAsset)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditWatchlistAssetIntent.SaveWatchlistAsset)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditWatchlistAssetIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel(id: String?): AddEditWatchlistAssetViewModel {
        val route = AddEditWatchlistAssetRoute(id, if (id == null) R.string.add_watchlist_asset else R.string.edit_watchlist_asset)

        return AddEditWatchlistAssetViewModel(
            route,
            getUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }

}

