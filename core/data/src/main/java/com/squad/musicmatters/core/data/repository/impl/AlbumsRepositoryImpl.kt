package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.model.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumsRepositoryImpl @Inject constructor(
    songsRepository: SongsRepository

): AlbumsRepository {

    override fun fetchAlbums(): Flow<List<Album>> {
        TODO("Not yet implemented")
    }

    override fun fetchAlbumWithId(id: Long): Flow<Album> {
        TODO("Not yet implemented")
    }

}