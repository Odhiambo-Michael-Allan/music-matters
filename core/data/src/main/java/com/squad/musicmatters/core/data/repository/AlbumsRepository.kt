package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AlbumsRepository {
    fun fetchAlbums(): Flow<List<Album>>
    fun fetchAlbumWithId( id: Long ): Flow<Album>
}