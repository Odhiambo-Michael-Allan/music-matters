package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.MostPlayedSongsRepository
import com.squad.musicmatters.core.data.testDoubles.TestSongPlayCountEntryDao
import com.squad.musicmatters.core.database.dao.SongPlayCountEntryDao
import com.squad.musicmatters.core.database.model.SongPlayCountEntity
import com.squad.musicmatters.core.testing.repository.TestSongsRepository
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MostPlayedSongsRepositoryImplTest {

    private lateinit var songsPlayCountEntryDao: SongPlayCountEntryDao
    private lateinit var songsRepository: TestSongsRepository
    private lateinit var subject: MostPlayedSongsRepository

    @Before
    fun setUp() {
        songsPlayCountEntryDao = TestSongPlayCountEntryDao()
        songsRepository = TestSongsRepository()
        subject = MostPlayedSongsRepositoryImpl(
            songsPlayCountEntryDao = songsPlayCountEntryDao,
            songsRepository = songsRepository,
        )
    }

    @Test
    fun testMostPlayedSongsAreFetchedCorrectly() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        val entities = listOf(
            SongPlayCountEntity( songId = "song-id-1", numberOfTimesPlayed = 4 ),
            SongPlayCountEntity( songId = "song-id-2", numberOfTimesPlayed = 1 ),
            SongPlayCountEntity( songId = "song-id-3", numberOfTimesPlayed = 5 ),
            SongPlayCountEntity( songId = "song-id-4", numberOfTimesPlayed = 2 ),
            SongPlayCountEntity( songId = "song-id-5", numberOfTimesPlayed = 3 ),
        )

        songsRepository.sendSongs( emptyList() )

        assertTrue( subject.fetchSongsSortedByPlayCount().first().isEmpty() )

        songsRepository.sendSongs( songs )
        entities.forEach { songsPlayCountEntryDao.upsertEntity( it ) }

        assertEquals(
            listOf(
                "song-id-3",
                "song-id-1",
                "song-id-5",
                "song-id-4",
                "song-id-2"
            ),
            subject.fetchSongsSortedByPlayCount().first().map { it.id }
        )
    }

    @Test
    fun testSongIsInsertedCorrectly() = runTest {
        val songAlreadyPresentInStore = testSong( id = "song-id-present" )
        val songNotPresentInStore = testSong( id = "song-id-absent" )

        songsPlayCountEntryDao.upsertEntity(
            SongPlayCountEntity(
                songId = songAlreadyPresentInStore.id
            )
        )

        songsRepository.sendSongs(
            listOf( songAlreadyPresentInStore, songNotPresentInStore )
        )

        assertEquals( 1, subject.fetchSongsSortedByPlayCount().first().size )

        subject.addSongId( songAlreadyPresentInStore.id )

        assertEquals(
            2,
            songsPlayCountEntryDao
                .fetchEntriesSortedByPlayCount()
                .first()
                .find { it.songId == songAlreadyPresentInStore.id }!!
                .numberOfTimesPlayed
        )

        subject.addSongId( songNotPresentInStore.id )

        assertEquals(
            2,
            songsPlayCountEntryDao
                .fetchEntriesSortedByPlayCount()
                .first()
                .size
        )

        assertEquals(
            1,
            songsPlayCountEntryDao
                .fetchEntriesSortedByPlayCount()
                .first()
                .find { it.songId == songNotPresentInStore.id }!!
                .numberOfTimesPlayed
        )
    }

    @Test
    fun testDeleteSongWithId() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        val entities = listOf(
            SongPlayCountEntity( songId = "song-id-1", numberOfTimesPlayed = 4 ),
            SongPlayCountEntity( songId = "song-id-2", numberOfTimesPlayed = 1 ),
            SongPlayCountEntity( songId = "song-id-3", numberOfTimesPlayed = 5 ),
            SongPlayCountEntity( songId = "song-id-4", numberOfTimesPlayed = 2 ),
            SongPlayCountEntity( songId = "song-id-5", numberOfTimesPlayed = 3 ),
        )

        songsRepository.sendSongs( songs )
        entities.forEach { songsPlayCountEntryDao.upsertEntity( it ) }

        assertEquals(
            5,
            songsPlayCountEntryDao.fetchEntriesSortedByPlayCount().first().size
        )

        subject.deleteSongWithId( "song-id-3" )

        assertEquals(
            4,
            songsPlayCountEntryDao.fetchEntriesSortedByPlayCount().first().size
        )
    }

}