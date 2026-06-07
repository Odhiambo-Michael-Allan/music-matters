package com.squad.musicmatters.core.data.repository.impl

import androidx.annotation.WorkerThread
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.database.dao.QueueDao
import com.squad.musicmatters.core.database.model.QueueEntity
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val queueDao: QueueDao,
    private val songsRepository : SongsRepository,
    @param:Dispatcher( MusicMattersDispatchers.IO )
    private val ioDispatcher: CoroutineDispatcher,
) : QueueRepository {

    override fun fetchSongsInQueueSortedByPosition(): Flow<List<Song>> =
        combine(
            songsRepository.fetchSongs(),
            queueDao.fetchQueueEntitiesSortedByPosition()
        ) { songs, queueEntities ->
            val songsMap = songs.associateBy { it.id }
            queueEntities.mapNotNull { queueEntity ->
                songsMap[ queueEntity.songId ]
            }
        }
//        queueDao.fetchQueueEntitiesSortedByPosition().flatMapLatest { queueEntities ->
//            val idsOfSongsInQueue = queueEntities.map { it.songId }
//            songsRepository.fetchSongs().map { songs ->
//                songs.filter { it.id in idsOfSongsInQueue }.sortWith( queueEntities )
//            }
//        }

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

    @WorkerThread
    override suspend fun shuffleSongsInQueue(
        currentlyPlayingSong: Song
    ) {
        withContext( ioDispatcher ) {
            val shuffledSongs = fetchSongsInQueueSortedByPosition()
                .first()
                .shuffled()
                .toMutableList()
            val removed = shuffledSongs.removeIf { it.id == currentlyPlayingSong.id }
            if ( removed ) shuffledSongs.add( 0, currentlyPlayingSong )
            saveQueue( shuffledSongs )
        }
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