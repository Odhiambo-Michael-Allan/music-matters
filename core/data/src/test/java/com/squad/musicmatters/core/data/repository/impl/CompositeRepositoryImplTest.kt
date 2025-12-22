package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.CompositeRepository
import com.squad.musicmatters.core.testing.repository.TestMostPlayedSongsRepository
import com.squad.musicmatters.core.testing.repository.TestPlayHistoryRepository
import com.squad.musicmatters.core.testing.repository.TestQueueRepository
import com.squad.musicmatters.core.testing.repository.TestSongsAdditionalMetadataRepository
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CompositeRepositoryImplTest {

    private lateinit var mostPlayedSongsRepository: TestMostPlayedSongsRepository
    private lateinit var playHistoryRepository: TestPlayHistoryRepository
    private lateinit var songsAdditionalMetadataRepository: TestSongsAdditionalMetadataRepository
    private lateinit var queueRepository: TestQueueRepository
    private lateinit var subject: CompositeRepository

    @Before
    fun setUp() {
        mostPlayedSongsRepository = TestMostPlayedSongsRepository()
        playHistoryRepository = TestPlayHistoryRepository()
        songsAdditionalMetadataRepository = TestSongsAdditionalMetadataRepository()
        queueRepository = TestQueueRepository()
        subject = CompositeRepositoryImpl(
            mostPlayedSongsRepository = mostPlayedSongsRepository,
            playHistoryRepository = playHistoryRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
            queueRepository = queueRepository
        )
    }

    @Test
    fun testDeleteSong() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" ),
        )
        mostPlayedSongsRepository.sendSongs( songs )
        playHistoryRepository.sendSongs( songs )
        songsAdditionalMetadataRepository.sendMetadata(
            songs.map {
                SongAdditionalMetadataInfo(
                    songId = it.id,
                    codec = "",
                    bitrate = 0L,
                    bitsPerSample = 0L,
                    samplingRate = 0f,
                    genre = ""
                )
            }
        )
        queueRepository.sendSongs( songs )

        subject.deleteSongWithId( "song-id-3" )

        assertEquals(
            4,
            mostPlayedSongsRepository.fetchSongsSortedByPlayCount().first().size
        )
        assertEquals(
            4,
            playHistoryRepository.fetchSongsSortedByTimePlayed().first().size
        )
        assertEquals(
            4,
            songsAdditionalMetadataRepository.fetchAdditionalMetadataEntries().first().size
        )
        assertEquals(
            4,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
    }

}