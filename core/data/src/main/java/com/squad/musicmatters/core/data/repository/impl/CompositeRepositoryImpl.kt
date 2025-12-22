package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.CompositeRepository
import com.squad.musicmatters.core.data.repository.MostPlayedSongsRepository
import com.squad.musicmatters.core.data.repository.PlayHistoryRepository
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import javax.inject.Inject

class CompositeRepositoryImpl @Inject constructor(
    private val mostPlayedSongsRepository: MostPlayedSongsRepository,
    private val playHistoryRepository: PlayHistoryRepository,
    private val queueRepository: QueueRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : CompositeRepository {

    override suspend fun deleteSongWithId( songId: String ) {
        mostPlayedSongsRepository.deleteSongWithId( songId )
        playHistoryRepository.deleteSongWithId( songId )
        queueRepository.removeSongWithId( songId )
        songsAdditionalMetadataRepository.deleteEntryWithId( songId )
    }

}