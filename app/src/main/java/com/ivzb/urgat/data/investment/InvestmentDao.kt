package com.ivzb.urgat.data.investment

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {

    @Query("" +
            "SELECT * FROM investments " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC, id ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<InvestmentEntity>>

    @Query("" +
            "SELECT * FROM investments " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd AND (" +
                "name LIKE '%' || :searchQuery || '%' OR " +
                "shares LIKE '%' || :searchQuery || '%' OR " +
                "category LIKE '%' || :searchQuery || '%' OR " +
                "currency LIKE '%' || :searchQuery || '%'" +
            ")" +
            "ORDER BY date DESC, id ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE isDeleted = 0 ORDER BY date DESC, id ASC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InvestmentEntity>)

    @Upsert
    suspend fun upsert(entity: InvestmentEntity)

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getById(id: String): InvestmentEntity?

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT currency FROM investments WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCurrency(): String?

}
