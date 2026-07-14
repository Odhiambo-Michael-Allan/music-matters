package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.SortArtistsBy
import kotlinx.coroutines.flow.Flow

interface ArtistsRepository {
    fun fetchArtists(
        sortArtistsBy: SortArtistsBy? = null,
        sortArtistsInReverse: Boolean = false,
    ): Flow<List<Artist>>

    fun fetchArtistWithId( id: Long ): Flow<Artist>
    fun searchArtistsMatching(
        query: String,
        sortArtistsBy: SortArtistsBy?,
        sortArtistsInReverse: Boolean = false,
    ): Flow<List<Artist>>
}