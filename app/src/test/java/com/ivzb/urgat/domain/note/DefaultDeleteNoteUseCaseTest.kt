package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultDeleteNoteUseCaseTest {

    private lateinit var notesRepository: NotesRepository
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @Before
    fun setUp() {
        notesRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        deleteNoteUseCase = DefaultDeleteNoteUseCase(notesRepository)
    }

    @Test
    fun `invoke should call repository delete with correct parameters`() = runTest {
        val id = "123"

        deleteNoteUseCase(id)

        coVerify(exactly = 1) {
            notesRepository.delete(id)
        }
    }
}
