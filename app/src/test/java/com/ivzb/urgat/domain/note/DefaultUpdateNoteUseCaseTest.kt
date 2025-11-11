package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultUpdateNoteUseCaseTest {

    private lateinit var notesRepository: NotesRepository
    private lateinit var updateNoteUseCase: UpdateNoteUseCase

    @Before
    fun setUp() {
        notesRepository = mockk(relaxed = true) // relaxed to ignore unmocked calls
        updateNoteUseCase = DefaultUpdateNoteUseCase(notesRepository)
    }

    @Test
    fun `invoke should call repository update with correct parameters`() = runTest {
        val id = "123"
        val content = "Note"

        updateNoteUseCase(id, content)

        coVerify(exactly = 1) {
            notesRepository.update(id, content)
        }
    }
}
