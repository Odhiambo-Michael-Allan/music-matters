package com.squad.musicmatters.core.data.repository.impl

import android.util.Log
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.database.dao.QueueDao
import com.squad.musicmatters.core.database.model.QueueEntity
import com.squad.musicmatters.core.model.QueueEntry
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

private const val TAG = "QUEUE-REPOSITORY"

class QueueRepositoryImpl @Inject constructor(
    private val queueDao: QueueDao,
    private val songsRepository : SongsRepository,
) : QueueRepository {

    override fun fetchSongsSortedByCurrentPosition(): Flow<List<Song>> =
        combine(
            songsRepository.fetchSongs(),
            queueDao.fetchEntitiesSortedByCurrentPositionInQueue()
        ) { songs, queueEntities ->
            val songsMap = songs.associateBy { it.id }
            queueEntities.mapNotNull { queueEntity ->
                songsMap[ queueEntity.songId ]
            }
        }

    override fun fetchSongsSortedByOriginalPosition(): Flow<List<Song>> =
        combine(
            songsRepository.fetchSongs(),
            queueDao.fetchEntitiesSortedByOriginalPositionInQueue()
        ) { songs, queueEntities ->
            val songsMap = songs.associateBy { it.id }
            queueEntities.mapNotNull { queueEntity ->
                songsMap[ queueEntity.songId ]
            }
        }

    override suspend fun saveQueue( queue: List<QueueEntry> ) {
        clearQueue()
        queueDao.upsertQueueEntities(
            queue.mapIndexed { index, queueEntry ->
                QueueEntity(
                    songId = queueEntry.songId,
                    positionInQueue = index,
                    originalPositionInQueue = queueEntry.originalPositionInQueue,
                )
            }
        )
    }

    override suspend fun removeSongWithId(id: String ) {
        queueDao.deleteEntryWithId( id )
    }

    override suspend fun clearQueue() {
        queueDao.clearQueue()
    }

}
