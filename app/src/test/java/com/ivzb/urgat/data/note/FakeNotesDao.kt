package com.ivzb.urgat.data.note

import android.database.Cursor
import android.database.MatrixCursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNoteDao : NoteDao {

    private val items = mutableListOf<NoteEntity>()
    private val _flow = MutableStateFlow<List<NoteEntity>>(emptyList())
    private var _cursor = MatrixCursor(arrayOf())

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<NoteEntity>> =
        _flow.map { it.filter { it.createdAt in dateStart..dateEnd }.take(limit) }

    override fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<NoteEntity>> =
        _flow.map { list ->
            list.filter { it.createdAt in dateStart..dateEnd && it.content.contains(searchQuery, ignoreCase = true) }.take(limit)
        }

    override fun stream(): Cursor = _cursor

    override suspend fun insertAll(items: List<NoteEntity>) {
        this.items.addAll(items)
        _flow.value = items.toList()
    }

    override suspend fun getById(id: String): NoteEntity? =
        items.find { it.id == id }

    override suspend fun deleteById(id: String): Int {
        items.removeAll { it.id == id }
        _flow.value = items.toList()
        return 1
    }

    override suspend fun upsert(entity: NoteEntity) {
        val index = items.indexOfFirst { it.id == entity.id }
        if (index >= 0) items[index] = entity else items.add(entity)
        _flow.value = items.toList()
    }

}