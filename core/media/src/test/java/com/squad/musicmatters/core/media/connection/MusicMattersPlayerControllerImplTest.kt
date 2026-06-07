package com.squad.musicmatters.core.media.connection

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.squad.musicmatters.core.data.repository.impl.CompositeRepositoryImpl
import com.squad.musicmatters.core.testing.connection.FakePlayerConnector
import com.squad.musicmatters.core.testing.repository.TestMostPlayedSongsRepository
import com.squad.musicmatters.core.testing.repository.TestPlayHistoryRepository
import com.squad.musicmatters.core.testing.repository.TestPreferencesDataSource
import com.squad.musicmatters.core.testing.repository.TestQueueRepository
import com.squad.musicmatters.core.testing.repository.TestSongsAdditionalMetadataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
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

class MusicMattersPlayerControllerImplTest {

    private lateinit var playerConnector: FakePlayerConnector
    private lateinit var mostPlayedSongsRepository: TestMostPlayedSongsRepository
    private lateinit var playHistoryRepository: TestPlayHistoryRepository
    private lateinit var songsAdditionalMetadataRepository: TestSongsAdditionalMetadataRepository
    private lateinit var queueRepository: TestQueueRepository
    private lateinit var preferencesDataSource: TestPreferencesDataSource
    private lateinit var subject: MusicMattersPlayerController


    @OptIn( ExperimentalCoroutinesApi::class )
    @Before
    fun setup() {
        playerConnector = FakePlayerConnector()
        mostPlayedSongsRepository = TestMostPlayedSongsRepository()
        playHistoryRepository = TestPlayHistoryRepository()
        songsAdditionalMetadataRepository = TestSongsAdditionalMetadataRepository()
        queueRepository = TestQueueRepository()
        preferencesDataSource = TestPreferencesDataSource()
        subject = MusicMattersPlayerControllerImpl(
            playerConnector = playerConnector,
            queueRepository = queueRepository,
            userPreferencesDataSource = preferencesDataSource,
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
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        assertEquals(
            1,
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
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        assertEquals(
            1,
            playerConnector.player.mediaItemCount
        )
        assertEquals(
            testSongs.first().id,
            queueRepository.fetchSongsInQueueSortedByPosition().first().first().id
        )
        subject.playSongNext(
            testSongs.last()
        )
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        assertEquals(
            testSongs.last().id,
            queueRepository.fetchSongsInQueueSortedByPosition().first()[1].id
        )
        assertEquals(
            1,
            playerConnector.player.mediaItemCount
        )
    }

    @Test
    fun testShuffleAndPlay() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        subject.shuffleAndPlay( testSongs )
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        assertEquals(
            1,
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
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        subject.playNextSong()
        assertEquals( 0, playerConnector.player.currentMediaItemIndex )
        assertEquals(
            testSongs[1].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        subject.playNextSong()
        assertEquals( 0, playerConnector.player.currentMediaItemIndex )
        assertEquals(
            testSongs[2].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
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
            queueRepository
                .fetchSongsInQueueSortedByPosition()
                .first()
                .first()
                .id
        )
        subject.playNextSong()
        // testSongs[1] is currently playing
        assertEquals(
            testSongs[1].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        subject.shuffleSongsInQueue()
        // The previously playing song [testSongs[1]] should have been moved to the
        // first position after shuffle.
        assertEquals(
            testSongs[1].id,
            queueRepository
                .fetchSongsInQueueSortedByPosition()
                .first()
                .first()
                .id
        )
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
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
        assertTrue( subject.queue.value.isEmpty() )

    }

    @Test
    fun testSetRepeatMode() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.setLoopMode( LoopMode.Song )
        testSongs.forEach { subject.addToQueue( it ) }

        assertEquals( Player.REPEAT_MODE_ONE, playerConnector.player.repeatMode )
        subject.playNextSong( ignoreLoopMode = false )
        assertEquals(
            testSongs.first().id,
            playerConnector.player.currentMediaItem?.mediaId
        )

        preferencesDataSource.setLoopMode( LoopMode.None )
        assertEquals( Player.REPEAT_MODE_OFF, playerConnector.player.repeatMode )
        subject.playNextSong( ignoreLoopMode = false )
        assertEquals(
            testSongs[1].id,
            playerConnector.player.currentMediaItem?.mediaId
        )

        preferencesDataSource.setLoopMode( LoopMode.Queue )
        assertEquals( Player.REPEAT_MODE_ALL, playerConnector.player.repeatMode )
        ( 0 until testSongs.size - 1 ).forEach { subject.playNextSong( ignoreLoopMode = false ) }
        assertEquals(
            testSongs.first().id,
            playerConnector.player.currentMediaItem?.mediaId
        )
    }

    @Test
    fun testPlayPreviousSong() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            subject.addToQueue( it )
        } // [testSongs[0]] is playing
        subject.playNextSong( ignoreLoopMode = true ) // [testSongs[1]] is playing
        playerConnector.setCurrentDurationInPlayback( 1000 )
        subject.playPreviousSong() // [testSongs[0]] is playing
        assertEquals(
            testSongs[0].id,
            playerConnector.player.currentMediaItem?.mediaId
        )

        subject.playNextSong( ignoreLoopMode = true ) // [testSongs[1]] is playing
        playerConnector.setCurrentDurationInPlayback( 2000 )
        subject.playPreviousSong() // [testSongs[1]] has been restarted
        assertEquals(
            testSongs[1].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        assertEquals(
            0L,
            playerConnector.player.currentPosition
        )

    }

    @Test
    fun testWhenNowPlayingSongIsDeleted_theNextSongInQueueIsPlayed() = runTest {
        queueRepository.sendSongs( testSongs )
        preferencesDataSource.sendUserData( emptyUserData )
        subject.playSong(
            song = testSongs.first(),
            songs = testSongs,
            shuffle = false
        )
        subject.deleteSong( testSongs.first() )
        assertEquals(
            testSongs[1].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
        subject.deleteSong( testSongs[1] )
        assertEquals(
            testSongs[2].id,
            playerConnector.player.currentMediaItem?.mediaId
        )
    }

    @Test
    fun testMoveSong() = runTest {
        queueRepository.sendSongs( testSongs )
        preferencesDataSource.sendUserData( emptyUserData )
        subject.playSong(
            song = testSongs.first(),
            songs = testSongs,
            shuffle = false
        )
        subject.moveSong( 0, 3 )
        assertEquals(
            testSongs[1].id,
            queueRepository
                .fetchSongsInQueueSortedByPosition()
                .first()
                .first()
                .id
        )
        assertEquals(
            testSongs.first().id,
            queueRepository
                .fetchSongsInQueueSortedByPosition()
                .first()[3].id
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

private class TestSongToMediaItemConverter : SongToMediaItemConverter {
    override fun convert( song: Song ): MediaItem =
        MediaItem.Builder().apply {
            setMediaId( song.id )
        }.build()
}

private val testSongs = listOf(
    testSong( id = "song-id-1" ),
    testSong( id = "song-id-2" ),
    testSong( id = "song-id-3" ),
    testSong( id = "song-id-4" ),
    testSong( id = "song-id-5" )
)