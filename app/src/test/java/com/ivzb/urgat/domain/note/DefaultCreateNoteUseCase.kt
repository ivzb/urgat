package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateNoteUseCaseTest {

    private lateinit var notesRepository: NotesRepository
    private lateinit var createNoteUseCase: CreateNoteUseCase

    @Before
    fun setUp() {
        notesRepository = mockk(relaxed = true)
        createNoteUseCase = DefaultCreateNoteUseCase(notesRepository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "Note"

        createNoteUseCase(name)

        coVerify(exactly = 1) {
            notesRepository.create(name)
        }
    }
}