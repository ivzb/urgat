package com.ivzb.urgat.data.base

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {

    fun getStream(limit: Int): Flow<List<T>>

    fun getStream(limit: Int, searchQuery: String): Flow<List<T>>

    suspend fun get(id: String): T?

    suspend fun delete(id: String)
}