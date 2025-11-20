package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteNoteUseCaseTest {

    private lateinit var repository: NotesRepository
    private lateinit var deleteUseCase: DeleteNoteUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteUseCase = DefaultDeleteNoteUseCase(repository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteUseCase(id)

        coVerify(exactly = 1) {
            repository.delete(id)
        }
    }
}
