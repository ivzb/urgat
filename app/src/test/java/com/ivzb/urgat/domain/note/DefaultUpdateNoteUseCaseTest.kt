package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultUpdateNoteUseCaseTest {

    private lateinit var repository: NotesRepository
    private lateinit var updateUseCase: UpdateNoteUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateUseCase = DefaultUpdateNoteUseCase(repository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val content = "Note"

        updateUseCase(id, content)

        coVerify(exactly = 1) {
            repository.update(id, content)
        }
    }
}
