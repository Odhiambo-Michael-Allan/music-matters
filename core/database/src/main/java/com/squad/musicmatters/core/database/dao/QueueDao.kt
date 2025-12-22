package com.squad.musicmatters.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.squad.musicmatters.core.database.model.QueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {

    @Upsert
    suspend fun upsertQueueEntity( queueEntity: QueueEntity )

    @Upsert
    suspend fun upsertQueueEntities( queueEntities: List<QueueEntity> )

    @Query(
        value = """
            SELECT * FROM queue
            ORDER BY position_in_queue ASC
        """
    )
    fun fetchQueueEntitiesSortedByPosition(): Flow<List<QueueEntity>>

    @Query(
        value = """
            DELETE FROM queue WHERE song_id = :songId
        """
    )
    suspend fun deleteEntryWithId( songId: String )

    @Query(
        value = "DELETE FROM queue"
    )
    suspend fun clearQueue()

}