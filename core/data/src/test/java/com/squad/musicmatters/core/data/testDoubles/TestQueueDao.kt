package com.squad.musicmatters.core.data.testDoubles

import com.squad.musicmatters.core.database.dao.QueueDao
import com.squad.musicmatters.core.database.model.QueueEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestQueueDao : QueueDao {

    private val queueEntitiesFlow =
        MutableStateFlow( emptyList<QueueEntity>() )

    override suspend fun upsertQueueEntity( queueEntity: QueueEntity ) {
        queueEntitiesFlow.update { oldValues ->
            // New values come first so they overwrite old values.
            ( listOf( queueEntity ) + oldValues )
                .distinctBy( QueueEntity::songId )
                .sortedBy { it.positionInQueue }
        }
    }

    override suspend fun upsertQueueEntities( queueEntities: List<QueueEntity> ) {
        queueEntitiesFlow.update { oldValues ->
            ( queueEntities + oldValues )
                .distinctBy( QueueEntity::songId )
                .sortedBy( QueueEntity::songId )
        }
    }

    override fun fetchQueueEntitiesSortedByPosition(): Flow<List<QueueEntity>> =
        queueEntitiesFlow.map { entities ->
            entities.sortedBy { it.positionInQueue }
        }

    override suspend fun deleteEntryWithId( songId: String ) {
        queueEntitiesFlow.update { oldValues ->
            val newValues = oldValues.toMutableList()
            newValues.removeIf { it.songId == songId }
            newValues
        }
    }

    override suspend fun clearQueue() {
        queueEntitiesFlow.update { emptyList() }
    }

}