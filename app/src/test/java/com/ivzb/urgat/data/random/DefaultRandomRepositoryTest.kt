package com.ivzb.urgat.data.random

import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DefaultRandomRepositoryTest {

    private lateinit var repository: RandomRepository

    @Before
    fun setUp() {
        repository = DefaultRandomRepository()
    }

    @Test
    fun `uuid should return a valid uuid`() {
        val uuid = repository.uuid()

        assertEquals(uuid, UUID.fromString(uuid).toString())
    }

    @Test
    fun `uuid should return a different value on every call`() {
        val uuids = (1..100).map { repository.uuid() }

        assertEquals(100, uuids.toSet().size)
    }

    @Test
    fun `uuid should not return the same value twice in a row`() {
        assertNotEquals(repository.uuid(), repository.uuid())
    }

}

