package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.R
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveNotesUseCaseTest {

    private lateinit var repository: NotesRepository
    private lateinit var observeUseCase: ObserveNotesUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveNotesUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("note")
        val limit = 10
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Note("1", "Note 1", false, 1L),
                Note("2", "Note 2", false, 1L)
            )
        )

        every { repository.getStream(limit, "note") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(2, items.size)
        assertEquals("Note 1", items[0].content)
        assertEquals("Note 2", items[1].content)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.getStream(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
        assertEquals(R.string.error_notes, (first as Async.Error).errorMessage)
    }
}
