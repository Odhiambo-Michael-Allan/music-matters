package com.squad.musicmatters.core.data.testDoubles

import com.squad.musicmatters.core.database.dao.PlaylistDao
import com.squad.musicmatters.core.database.model.PlaylistEntity
import com.squad.musicmatters.core.database.model.PopulatedPlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestPlaylistDao : PlaylistDao() {

    private val entitiesStateFlow = MutableStateFlow( emptyList<PlaylistEntity>() )

    override fun fetchPlaylists(): Flow<List<PopulatedPlaylistEntity>> =
        entitiesStateFlow.map { entityList ->
            entityList.map {
                PopulatedPlaylistEntity(
                    playlistEntity = it,
                    entries = emptyList()
                )
            }
        }

    override fun fetchPlaylistWithId( id: String ): Flow<PopulatedPlaylistEntity?> =
        entitiesStateFlow.map { entityList ->
            entityList.find { it.id == id }?.let {
                PopulatedPlaylistEntity(
                    playlistEntity = it,
                    entries = emptyList()
                )
            }
        }

    override suspend fun playlistExists( playlistId: String ): Boolean =
        entitiesStateFlow.first().find { it.id == playlistId }?.let { true } ?: false

    override suspend fun insert( entity: PlaylistEntity ) {
        entitiesStateFlow.update {
            it + entity
        }
    }

    override suspend fun insertAll( vararg entity: PlaylistEntity ) {
        entitiesStateFlow.update {
            it + entity
        }
    }

    override suspend fun insertAll( collection: Collection<PlaylistEntity> ) {
        entitiesStateFlow.update {
            it + collection
        }
    }

    override suspend fun update( entity: PlaylistEntity ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.removeIf { it.id == entity.id }
        currentEntities.add( entity )
        entitiesStateFlow.tryEmit( currentEntities )
    }

    override suspend fun delete( entity: PlaylistEntity ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.removeIf { it.id == entity.id }
        entitiesStateFlow.tryEmit( currentEntities )
    }
}