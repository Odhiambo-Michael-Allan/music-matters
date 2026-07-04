package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.SortAlbumsBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AlbumsRepository {
    fun fetchAlbums(
        sortAlbumsBy: SortAlbumsBy? = null,
        sortAlbumsInReverse: Boolean? = null,
    ): Flow<List<Album>>
    fun fetchAlbumWithId( id: Long ): Flow<Album>
}