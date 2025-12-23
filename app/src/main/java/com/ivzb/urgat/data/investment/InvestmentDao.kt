package com.ivzb.urgat.data.investment

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {

    @Query("SELECT * FROM investments WHERE isDeleted = 0 ORDER BY date DESC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<InvestmentEntity>>

    @Query("" +
            "SELECT * FROM investments " +
            "WHERE isDeleted = 0 AND (" +
                "name LIKE '%' || :searchQuery || '%' OR " +
                "shares LIKE '%' || :searchQuery || '%' OR " +
                "category LIKE '%' || :searchQuery || '%' OR " +
                "currency LIKE '%' || :searchQuery || '%'" +
            ")" +
            "ORDER BY date DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, searchQuery: String): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE isDeleted = 0 ORDER BY date DESC")
    fun streamAll(): Cursor

    @Upsert
    suspend fun upsert(entity: InvestmentEntity)

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getById(id: String): InvestmentEntity?

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
