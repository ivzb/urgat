package com.ivzb.urgat.data.investment

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {

    @Query("SELECT * FROM investments WHERE isDeleted = 0 ORDER BY name ASC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<InvestmentEntity>>

    @Query("" +
            "SELECT * FROM investments " +
            "WHERE isDeleted = 0 AND name LIKE '%' || :searchQuery || '%' OR amount LIKE '%' || :searchQuery || '%'" +
            "ORDER BY name ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, searchQuery: String): Flow<List<InvestmentEntity>>

    @Upsert
    suspend fun upsert(investment: InvestmentEntity)

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getById(id: String): InvestmentEntity?

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
