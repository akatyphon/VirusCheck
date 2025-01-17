package com.madinaappstudio.viruscheck.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import retrofit2.http.Path

@Dao
interface HistoryDao {

    @Insert
    suspend fun insertHistory(historyEntity: HistoryEntity)

    @Delete
    suspend fun deleteHistory(historyEntity: HistoryEntity)

    @Query("SELECT * FROM HistoryTable ORDER BY id DESC")
    suspend fun getAllHistory(): List<HistoryEntity>

    @Query("DELETE FROM HistoryTable")
    suspend fun deleteAllHistory()


}