package com.ivzb.urgat.data.base

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {

    fun observe(limit: Int): Flow<List<T>>

    fun observe(limit: Int, searchQuery: String): Flow<List<T>>

    suspend fun get(id: String): T?

    suspend fun delete(id: String)

    companion object {

        const val NO_SEARCH_QUERY = ""

        const val NO_ITEMS_LIMIT = -1

    }
}