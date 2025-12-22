package com.squad.musicmatters.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.squad.musicmatters.core.database.model.PlayHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayHistoryDao {

    @Upsert
    suspend fun upsertPlayHistoryEntity(playHistoryEntity: PlayHistoryEntity )

    @Query(
        value = """
            SELECT * FROM play_history
            ORDER BY timePlayed DESC
        """
    )
    fun fetchPlayHistoryEntitiesSortedByTimePlayed(): Flow<List<PlayHistoryEntity>>

    @Query(
        value = """
            DELETE FROM play_history
            WHERE song_id = :songId
        """
    )
    suspend fun deletePlayHistoryEntityWithSongId( songId: String )

}