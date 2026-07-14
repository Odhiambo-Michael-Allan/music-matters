package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.CompositeRepository
import com.squad.musicmatters.core.testing.repository.FakeMostPlayedSongsRepository
import com.squad.musicmatters.core.testing.repository.FakePlayHistoryRepository
import com.squad.musicmatters.core.testing.repository.FakeQueueRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.SongMetadata
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CompositeRepositoryImplTest {

    private lateinit var mostPlayedSongsRepository: FakeMostPlayedSongsRepository
    private lateinit var playHistoryRepository: FakePlayHistoryRepository
    private lateinit var songsAdditionalMetadataRepository: FakeSongsMetadataRepository
    private lateinit var queueRepository: FakeQueueRepository
    private lateinit var subject: CompositeRepository

    @Before
    fun setUp() {
        mostPlayedSongsRepository = FakeMostPlayedSongsRepository()
        playHistoryRepository = FakePlayHistoryRepository()
        songsAdditionalMetadataRepository = FakeSongsMetadataRepository()
        queueRepository = FakeQueueRepository()
        subject = CompositeRepositoryImpl(
            mostPlayedSongsRepository = mostPlayedSongsRepository,
            playHistoryRepository = playHistoryRepository,
            songsMetadataRepository = songsAdditionalMetadataRepository,
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
                SongMetadata(
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
            songsAdditionalMetadataRepository.fetchMetadata().first().size
        )
        assertEquals(
            4,
            queueRepository.fetchSongsSortedByCurrentPosition().first().size
        )
    }

}