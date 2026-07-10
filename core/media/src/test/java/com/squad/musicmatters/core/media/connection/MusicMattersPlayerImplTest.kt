package com.squad.musicmatters.core.media.connection

import androidx.media3.common.MediaItem
import com.squad.musicmatters.core.media.media.extensions.getMediaItems
import com.squad.musicmatters.core.testing.connection.FakePlayerConnector
import com.squad.musicmatters.core.testing.repository.TestMostPlayedSongsRepository
import com.squad.musicmatters.core.testing.repository.TestPlayHistoryRepository
import com.squad.musicmatters.core.testing.repository.FakeUserPreferencesRepository
import com.squad.musicmatters.core.testing.repository.FakeQueueRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.Song
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MusicMattersPlayerImplTest {

    private lateinit var playerConnector: FakePlayerConnector
    private lateinit var mostPlayedSongsRepository: TestMostPlayedSongsRepository
    private lateinit var playHistoryRepository: TestPlayHistoryRepository
    private lateinit var songsAdditionalMetadataRepository: FakeSongsMetadataRepository
    private lateinit var queueRepository: FakeQueueRepository
    private lateinit var preferencesDataSource: FakeUserPreferencesRepository
    private lateinit var subject: MusicMattersPlayer


    @OptIn( ExperimentalCoroutinesApi::class )
    @Before
    fun setup() {
        playerConnector = FakePlayerConnector()
        mostPlayedSongsRepository = TestMostPlayedSongsRepository()
        playHistoryRepository = TestPlayHistoryRepository()
        songsAdditionalMetadataRepository = FakeSongsMetadataRepository()
        queueRepository = FakeQueueRepository()
        preferencesDataSource = FakeUserPreferencesRepository()
        subject = MusicMattersPlayerImpl(
            playerConnector = playerConnector,
            songToMediaItemConverter = TestSongToMediaItemConverter(),
            dispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun testAddToQueue() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            subject.addToQueue( it )
        }
        subject.addToQueue( testSongs.first() ) // NO DUPLICATES!
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
        assertEquals(
            testSongs.first().id,
            playerConnector.player.currentMediaItem!!.mediaId
        )

        assertEquals(
            0,
            playerConnector.player.currentMediaItemIndex
        )
    }

    @Test
    fun testPlayNext() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            subject.playSongNext( it )
        }
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
        assertEquals(
            testSongs.first().id,
            playerConnector.player.getMediaItems().first().mediaId
        )
        subject.playSongNext(
            testSongs.last()
        )
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
        assertEquals(
            testSongs.last().id,
            playerConnector.player.getMediaItems()[1].mediaId
        )
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
    }

    @Test
    fun testSeekToNext() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            subject.addToQueue( it )
        }
        assertEquals( 0, playerConnector.player.currentMediaItemIndex )
        assertEquals(
            testSongs.first().id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
        subject.playNextSong()
        assertEquals( 1, playerConnector.player.currentMediaItemIndex )
        assertEquals(
            testSongs[1].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
        subject.playNextSong()
        assertEquals( 2, playerConnector.player.currentMediaItemIndex )
        assertEquals(
            testSongs[2].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
    }

    @Test
    fun testShuffleSongsInQueue() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            subject.addToQueue( it )
        }
        // At this point, testSongs[0] is currently playing
        assertEquals(
            testSongs.first().id,
            playerConnector.player.currentMediaItem!!.mediaId
        )
        subject.playNextSong()
        // testSongs[1] is currently playing
        assertEquals(
            testSongs[1].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        subject.shuffleSongsInQueue( true )
        // The previously playing song [testSongs[1]] should have been moved to the
        // first position after shuffle.
        assertEquals(
            testSongs[1].id,
            playerConnector.player.getMediaItems().first().mediaId
        )
        assertEquals(
            testSongs.size,
            playerConnector.player.mediaItemCount
        )
    }

    @Test
    fun testClearQueue() = runTest {
        queueRepository.sendSongs( emptyList() )
        testSongs.forEach {
            subject.addToQueue( it )
        }
        subject.clearQueue()
        assertNull( playerConnector.player.currentMediaItem )
        assertTrue( subject.idsOfSongsInQueue.value.isEmpty() )

    }


    @Test
    fun testPlayPreviousSong() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            subject.addToQueue( it )
        } // [testSongs[0]] is playing
        subject.playNextSong() // [testSongs[1]] is playing
        subject.playPreviousSong() // [testSongs[0]] is playing
        assertEquals(
            testSongs[0].id,
            playerConnector.player.currentMediaItem?.mediaId
        )

    }

    @Test
    fun testWhenNowPlayingSongIsRemoved_theNextSongInQueueIsPlayed() = runTest {
        queueRepository.sendSongs( testSongs )
        preferencesDataSource.sendUserData( emptyUserData )
        subject.playSong(
            song = testSongs.first(),
            songs = testSongs,
        )
        subject.remove( testSongs.first() )
        assertEquals( testSongs.size - 1, playerConnector.player.mediaItemCount )
    }

    @Test
    fun testMoveSong() = runTest {
        queueRepository.sendSongs( testSongs )
        preferencesDataSource.sendUserData( emptyUserData )
        subject.playSong(
            song = testSongs.first(),
            songs = testSongs,
        )
        subject.moveSong( 0, 3 )
        assertEquals(
            testSongs[1].id,
            playerConnector.player
                .getMediaItems()
                .first()
                .mediaId
        )
    }

    @Test
    fun testSleepTimerIsSetCorrectly() = runTest {
        assertNull( subject.sleepTimer.first() )
        val timerDuration: Duration = ( 5000L ).toDuration( DurationUnit.MILLISECONDS )
        subject.setTimer( timerDuration )
        assertEquals(
            timerDuration,
            subject.sleepTimer.first()?.duration
        )
    }

}

internal class TestSongToMediaItemConverter : SongToMediaItemConverter {
    override fun convert( song: Song ): MediaItem =
        MediaItem.Builder().apply {
            setMediaId( song.id )
        }.build()
}

internal val testSongs = listOf(
    testSong( id = "song-id-1" ),
    testSong( id = "song-id-2" ),
    testSong( id = "song-id-3" ),
    testSong( id = "song-id-4" ),
    testSong( id = "song-id-5" )
)