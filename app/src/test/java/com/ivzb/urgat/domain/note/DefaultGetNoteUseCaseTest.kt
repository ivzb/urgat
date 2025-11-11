package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetNoteUseCaseTest {

    private lateinit var notesRepository: NotesRepository
    private lateinit var getNoteUseCase: GetNoteUseCase

    @Before
    fun setUp() {
        notesRepository = mockk()
        getNoteUseCase = DefaultGetNoteUseCase(notesRepository)
    }

    @Test
    fun `invoke should return note when repository returns note`() = runTest {
        val id = "123"
        val expectedNote = Note(id, "Note")
        coEvery { notesRepository.get(id) } returns expectedNote

        val result = getNoteUseCase(id)

        assertEquals(expectedNote, result)
        coVerify(exactly = 1) { notesRepository.get(id) }
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        val id = "999"
        coEvery { notesRepository.get(id) } returns null

        val result = getNoteUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { notesRepository.get(id) }
    }
}