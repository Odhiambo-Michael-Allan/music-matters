package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.PlayHistoryRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.database.dao.PlayHistoryDao
import com.squad.musicmatters.core.database.model.PlayHistoryEntity
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class PlayHistoryRepositoryImpl @Inject constructor(
    private val playHistoryDao: PlayHistoryDao,
    private val songsRepository: SongsRepository
) : PlayHistoryRepository {

    override fun fetchSongsSortedByTimePlayed(): Flow<List<Song>> =
        playHistoryDao.fetchPlayHistoryEntitiesSortedByTimePlayed().flatMapLatest { entities ->
            songsRepository.fetchSongs().map { songs ->
                val sortedListOfSongs = mutableListOf<Song>()
                entities.forEach { entity ->
                    songs.find { it.id == entity.songId }?.let { sortedListOfSongs.add( it ) }
                }
                sortedListOfSongs
            }
        }

    override suspend fun upsertSongWithId( songId: String, timePlayed: Instant ) {
        playHistoryDao.upsertPlayHistoryEntity(
            PlayHistoryEntity(
                songId = songId,
                timePlayed = timePlayed
            )
        )
    }

    override suspend fun deleteSongWithId( songId: String ) {
        playHistoryDao.deletePlayHistoryEntityWithSongId( songId )
    }

}