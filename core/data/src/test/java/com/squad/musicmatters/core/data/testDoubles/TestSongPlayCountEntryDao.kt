package com.squad.musicmatters.core.data.testDoubles

import com.squad.musicmatters.core.database.dao.SongPlayCountEntryDao
import com.squad.musicmatters.core.database.model.SongPlayCountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestSongPlayCountEntryDao : SongPlayCountEntryDao {

    private val entitiesStateFlow = MutableStateFlow( emptyList<SongPlayCountEntity>() )

    override fun fetchEntriesSortedByPlayCount(): Flow<List<SongPlayCountEntity>> =
        entitiesStateFlow.map {
            it.sortedByDescending(SongPlayCountEntity::numberOfTimesPlayed )
        }

    override suspend fun getPlayCountBySongId( songId: String ): SongPlayCountEntity? =
        entitiesStateFlow.first().find { it.songId == songId }

    override suspend fun incrementPlayCount( songId: String ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.find { it.songId == songId }?.let {
            currentEntities.removeIf { it.songId == songId }
            currentEntities.add(
                it.copy(
                    numberOfTimesPlayed = it.numberOfTimesPlayed + 1
                )
            )
        } ?: currentEntities.add(
            SongPlayCountEntity(
                songId = songId,
                numberOfTimesPlayed = 1
            )
        )
    }

    override suspend fun deleteEntryWithSongId( songId: String ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.removeIf { it.songId == songId }
        entitiesStateFlow.tryEmit( currentEntities )
    }

    override suspend fun upsertEntity( entity: SongPlayCountEntity ) {
        entitiesStateFlow.update { oldValues ->
            ( listOf( entity ) + oldValues )
                .distinctBy( SongPlayCountEntity::songId )
                .sortedByDescending { it.numberOfTimesPlayed }
        }
    }

}