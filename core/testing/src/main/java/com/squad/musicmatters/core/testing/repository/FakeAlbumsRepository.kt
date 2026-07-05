package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.utils.sortAlbums
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.SortAlbumsBy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class FakeAlbumsRepository : AlbumsRepository {

    private val albumsFlow: MutableSharedFlow<List<Album>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchAlbums(
        sortAlbumsBy: SortAlbumsBy?,
        sortAlbumsInReverse: Boolean?
    ): Flow<List<Album>> = albumsFlow.map { albums ->
        albums.sortAlbums(
            by = sortAlbumsBy ?: DefaultPreferences.SORT_ALBUMS_BY,
            reverse = sortAlbumsInReverse ?: false
        )
    }

    override fun fetchAlbumWithId( id: Long ): Flow<Album> =
        albumsFlow.map { albums ->
            albums.find { it.id == id }!!
        }

    fun sendAlbums( albums: List<Album> ) {
        albumsFlow.tryEmit( albums )
    }

}