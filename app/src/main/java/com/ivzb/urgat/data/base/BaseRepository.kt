package com.ivzb.urgat.data.base

import android.database.Cursor
import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {

    fun observe(limit: Int): Flow<List<T>>

    fun observe(limit: Int, searchQuery: String): Flow<List<T>>

    fun stream(): Cursor

    suspend fun get(id: String): T?

    suspend fun delete(id: String)

    companion object {

        const val NO_SEARCH_QUERY = ""

        const val NO_ITEMS_LIMIT = -1

    }
}