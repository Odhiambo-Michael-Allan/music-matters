package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.database.dao.QueueDao
import com.squad.musicmatters.core.database.model.QueueEntity
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val queueDao: QueueDao,
    private val songsRepository : SongsRepository
) : QueueRepository {

    override fun fetchSongsInQueueSortedByPosition(): Flow<List<Song>> =
        queueDao.fetchQueueEntitiesSortedByPosition().flatMapLatest { queueEntities ->
            val idsOfSongsInQueue = queueEntities.map { it.songId }
            songsRepository.fetchSongs().map { songs ->
                songs.filter { it.id in idsOfSongsInQueue }.sortWith( queueEntities )
            }
        }

    override suspend fun upsertSong(
        song: Song,
        posInQueue: Int
    ) {
        queueDao.upsertQueueEntity(
            QueueEntity(
                songId = song.id,
                positionInQueue = posInQueue
            )
        )
    }

    override suspend fun saveQueue( queue: List<Song> ) {
        clearQueue()
        queueDao.upsertQueueEntities(
            queue.mapIndexed { index, song ->
                QueueEntity(
                    songId = song.id,
                    positionInQueue = index
                )
            }
        )
    }

    override suspend fun removeSongWithId( id: String ) {
        queueDao.deleteEntryWithId( id )
    }

    override suspend fun clearQueue() {
        queueDao.clearQueue()
    }

}

private fun List<Song>.sortWith( queueEntities: List<QueueEntity> ): List<Song> {
    val sortedList = mutableListOf<Song>()
    queueEntities.forEach { queueEntity ->
        val correspondingSong = find { it.id == queueEntity.songId }
        correspondingSong?.let { sortedList.add( it ) }
    }
    return sortedList
}