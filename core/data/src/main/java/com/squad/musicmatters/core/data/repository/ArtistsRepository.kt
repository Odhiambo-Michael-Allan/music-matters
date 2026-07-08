package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.SortArtistsBy
import kotlinx.coroutines.flow.Flow

interface ArtistsRepository {
    fun fetchArtists(
        sortArtistsBy: SortArtistsBy? = null,
        sortArtistsInReverse: Boolean? = null,
    ): Flow<List<Artist>>

    fun fetchArtistWithId( id: Long ): Flow<Artist>
}