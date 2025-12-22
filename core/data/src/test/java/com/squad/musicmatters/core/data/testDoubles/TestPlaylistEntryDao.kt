package com.squad.musicmatters.core.data.testDoubles

import com.squad.musicmatters.core.database.dao.PlaylistEntryDao
import com.squad.musicmatters.core.database.model.PlaylistEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestPlaylistEntryDao : PlaylistEntryDao() {

    private val entitiesStateFlow = MutableStateFlow( emptyList<PlaylistEntryEntity>() )

    override fun fetchEntriesForPlaylistWithId( id: String ): Flow<List<PlaylistEntryEntity>> =
        entitiesStateFlow.map {
            it.filter { it.playlistId == id }
        }

    override suspend fun deleteEntry( playlistId: String, songId: String ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.removeIf { it.playlistId == playlistId && it.songId == songId }
        entitiesStateFlow.tryEmit( currentEntities )
    }

    override suspend fun removeEntriesForPlaylistWithId( id: String ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.removeIf { it.playlistId == id }
        entitiesStateFlow.tryEmit( currentEntities )
    }

    override suspend fun insert( entity: PlaylistEntryEntity ) {
        entitiesStateFlow.update {
            it + entity
        }
    }

    override suspend fun insertAll( vararg entity: PlaylistEntryEntity ) {
        entitiesStateFlow.update {
            it + entity
        }
    }

    override suspend fun insertAll( collection: Collection<PlaylistEntryEntity> ) {
        entitiesStateFlow.update {
            it + collection
        }
    }

    override suspend fun update( entity: PlaylistEntryEntity ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.removeIf { it.playlistId == entity.playlistId && it.songId == entity.songId }
        currentEntities.add( entity )
        entitiesStateFlow.tryEmit( currentEntities )
    }

    override suspend fun delete( entity: PlaylistEntryEntity ) {
        val currentEntities = entitiesStateFlow.first().toMutableList()
        currentEntities.removeIf { it.playlistId == entity.playlistId && it.songId == entity.songId }
        entitiesStateFlow.tryEmit( currentEntities )
    }

}