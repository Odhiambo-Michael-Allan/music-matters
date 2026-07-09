package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.common.di.IoScope
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.songs.MetadataStore
import com.squad.musicmatters.core.data.utils.sortGenres
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject



class SongsMetadataRepositoryImpl @Inject constructor(
    private val metadataStore: MetadataStore,
    @param:IoScope private val coroutineScope: CoroutineScope,
    songsRepository: SongsRepository,
) : SongsMetadataRepository {

    // Thread-safe in-memory cache to store metadata once looked up
    private val metadataCache = ConcurrentHashMap<String, SongMetadata>()

    private val metadataFlow: StateFlow<List<SongMetadata>> = songsRepository.fetchSongs()
        .map { songs ->
            // 1. Identify which songs are missing from our memory cache
            val missingSongs = songs.filter { !metadataCache.containsKey( it.id ) }

            // 2. Only fetch metadata for the missing files
            if ( missingSongs.isNotEmpty() ) {
                val fetchedMetadata = metadataStore.fetchMetadataFor( missingSongs )
                fetchedMetadata.forEach { metadata ->
                    metadataCache[ metadata.songId ] = metadata
                }
            }

            // 3. Map the original song list to the cached metadata
            songs.mapNotNull { metadataCache[ it.id ] }
        }.stateIn(
            scope = coroutineScope, // Runs safely on Dispatchers.IO
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = emptyList()
        )

    override fun fetchMetadata(): Flow<List<SongMetadata>> = metadataFlow

    override fun fetchGenres(
        sortGenresBy: SortGenresBy,
        reverse: Boolean,
    ): Flow<List<Genre>> = metadataFlow.map { metadata ->
        val genres = metadata.groupBy { it.genre }.map { ( genre, metadataList ) ->
            Genre(
                name = genre,
                numberOfTracks = metadataList.size
            )
        }
        genres.sortGenres( by = sortGenresBy, reverse = reverse )
    }

    override suspend fun deleteEntryWithId( id: String ) {
        metadataCache.remove( id )
    }
}





