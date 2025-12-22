package com.squad.musicmatters.core.data.testDoubles

import com.squad.musicmatters.core.database.dao.PlayHistoryDao
import com.squad.musicmatters.core.database.model.PlayHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestPlayHistoryDao : PlayHistoryDao {

    private val playHistoryEntitiesFlow =
        MutableStateFlow( emptyList<PlayHistoryEntity>() )

    override suspend fun upsertPlayHistoryEntity( playHistoryEntity: PlayHistoryEntity ) {
        playHistoryEntitiesFlow.update { oldValues ->
            ( listOf( playHistoryEntity ) + oldValues )
                .distinctBy( PlayHistoryEntity::songId )
                .sortedByDescending( PlayHistoryEntity::timePlayed )
        }
    }

    override fun fetchPlayHistoryEntitiesSortedByTimePlayed(): Flow<List<PlayHistoryEntity>> =
        playHistoryEntitiesFlow.map {
            it.sortedByDescending(PlayHistoryEntity::timePlayed )
        }

    override suspend fun deletePlayHistoryEntityWithSongId( songId: String ) {
        playHistoryEntitiesFlow.update { oldValues ->
            val newValues = oldValues.toMutableList()
            newValues.removeIf { it.songId == songId }
            newValues
        }
    }

}