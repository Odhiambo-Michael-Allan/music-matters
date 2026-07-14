package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.utils.sortArtists
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.SortArtistsBy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class FakeArtistsRepository : ArtistsRepository {

    private val artistsFlow: MutableSharedFlow<List<Artist>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchArtists(
        sortArtistsBy: SortArtistsBy?,
        sortArtistsInReverse: Boolean
    ): Flow<List<Artist>> = artistsFlow.map { artists ->
        artists.sortArtists(
            by = sortArtistsBy ?: DefaultPreferences.SORT_ARTISTS_BY,
            reverse = sortArtistsInReverse ?: false
        )
    }

    override fun fetchArtistWithId( id: Long ): Flow<Artist> =
        artistsFlow.map { artists ->
            artists.find { it.id == id }!!
        }

    override fun searchArtistsMatching(
        query: String,
        sortArtistsBy: SortArtistsBy?,
        sortArtistsInReverse: Boolean
    ): Flow<List<Artist>> = artistsFlow

    fun sendArtists( artists: List<Artist> ) {
        artistsFlow.tryEmit( artists )
    }

}