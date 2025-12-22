package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.MostPlayedSongsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.database.dao.SongPlayCountEntryDao
import com.squad.musicmatters.core.database.model.SongPlayCountEntity
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MostPlayedSongsRepositoryImpl @Inject constructor(
    private val songsPlayCountEntryDao: SongPlayCountEntryDao,
    private val songsRepository: SongsRepository,
) : MostPlayedSongsRepository {

    override fun fetchSongsSortedByPlayCount(): Flow<List<Song>> =
        songsPlayCountEntryDao.fetchEntriesSortedByPlayCount().flatMapLatest { entities ->
            songsRepository.fetchSongs().map { songs ->
                val sortedList = mutableListOf<Song>()
                entities.forEach { entity ->
                    songs.find { it.id == entity.songId }?.let { sortedList.add( it ) }
                }
                sortedList
            }
        }

    override suspend fun addSongId( songId: String ) {
        songsPlayCountEntryDao.getPlayCountBySongId( songId )
            ?.let {
                songsPlayCountEntryDao.upsertEntity(
                    it.copy(
                        numberOfTimesPlayed = it.numberOfTimesPlayed + 1
                    )
                )
            } ?: songsPlayCountEntryDao.upsertEntity(
                entity = SongPlayCountEntity(
                    songId = songId,
                )
            )
    }

    override suspend fun deleteSongWithId( songId: String ) {
        songsPlayCountEntryDao.deleteEntryWithSongId( songId )
    }
    
}