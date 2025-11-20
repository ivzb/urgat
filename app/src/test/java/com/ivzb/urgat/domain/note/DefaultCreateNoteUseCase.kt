package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultCreateNoteUseCaseTest {

    private lateinit var repository: NotesRepository
    private lateinit var createUseCase: CreateNoteUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        createUseCase = DefaultCreateNoteUseCase(repository)
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val name = "Note"

        createUseCase(name)

        coVerify(exactly = 1) {
            repository.create(name)
        }
    }
}