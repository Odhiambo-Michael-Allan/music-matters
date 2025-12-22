package com.squad.musicmatters.core.media.connection

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.squad.musicmatters.core.data.repository.impl.CompositeRepositoryImpl
import com.squad.musicmatters.core.testing.connection.FakeConnectable
import com.squad.musicmatters.core.testing.repository.TestMostPlayedSongsRepository
import com.squad.musicmatters.core.testing.repository.TestPlayHistoryRepository
import com.squad.musicmatters.core.testing.repository.TestPreferencesDataSource
import com.squad.musicmatters.core.testing.repository.TestQueueRepository
import com.squad.musicmatters.core.testing.repository.TestSongsAdditionalMetadataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MusicServiceConnectionImplTest {
    private lateinit var connectable: FakeConnectable
    private lateinit var mostPlayedSongsRepository: TestMostPlayedSongsRepository
    private lateinit var playHistoryRepository: TestPlayHistoryRepository
    private lateinit var songsAdditionalMetadataRepository: TestSongsAdditionalMetadataRepository
    private lateinit var queueRepository: TestQueueRepository
    private lateinit var preferencesDataSource: TestPreferencesDataSource
    private lateinit var musicServiceConnection: MusicServiceConnection


    @OptIn( ExperimentalCoroutinesApi::class )
    @Before
    fun setup() {
        connectable = FakeConnectable()
        mostPlayedSongsRepository = TestMostPlayedSongsRepository()
        playHistoryRepository = TestPlayHistoryRepository()
        songsAdditionalMetadataRepository = TestSongsAdditionalMetadataRepository()
        queueRepository = TestQueueRepository()
        preferencesDataSource = TestPreferencesDataSource()
        musicServiceConnection = MusicServiceConnectionImpl(
            connectable = connectable,
            queueRepository = queueRepository,
            compositeRepository = CompositeRepositoryImpl(
                mostPlayedSongsRepository = mostPlayedSongsRepository,
                playHistoryRepository = playHistoryRepository,
                songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
                queueRepository = queueRepository
            ),
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
            musicServiceConnection.addToQueue( it )
        }
        musicServiceConnection.addToQueue( testSongs.first() ) // NO DUPLICATES!
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        assertEquals(
            testSongs.size,
            connectable.player.mediaItemCount
        )
        assertEquals(
            testSongs.first().id,
            connectable.player.currentMediaItem!!.mediaId
        )

        assertEquals(
            0,
            connectable.player.currentMediaItemIndex
        )
    }

    @Test
    fun testPlayNext() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            musicServiceConnection.playNext( it )
        }
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        assertEquals(
            testSongs.size,
            connectable.player!!.mediaItemCount
        )
        assertEquals(
            testSongs.first().id,
            queueRepository.fetchSongsInQueueSortedByPosition().first().first().id
        )
        musicServiceConnection.playNext(
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
            testSongs.last().id,
            connectable.player!!.getMediaItemAt(1).mediaId
        )
    }

    @Test
    fun testShuffleAndPlay() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        musicServiceConnection.shuffleAndPlay( testSongs )
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
        assertEquals(
            testSongs.size,
            connectable.player!!.mediaItemCount
        )
    }

    @Test
    fun testSeekToNext() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            musicServiceConnection.addToQueue( it )
        }
        assertEquals( 0, connectable.player.currentMediaItemIndex )
        musicServiceConnection.playNextSong()
        assertEquals( 1, connectable.player.currentMediaItemIndex )
        musicServiceConnection.playNextSong()
        assertEquals( 2, connectable.player.currentMediaItemIndex )
    }

    @Test
    fun testShuffleSongsInQueue() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            musicServiceConnection.addToQueue( it )
        }
        connectable.player.seekToNext()  // testSongs[1] is currently playing
        musicServiceConnection.shuffleSongsInQueue()
        assertEquals( 0, connectable.player.currentMediaItemIndex )
        assertEquals(
            testSongs[1].id,
            connectable.player.currentMediaItem!!.mediaId
        )
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
    }

    @Test
    fun testMoveMediaItem() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            musicServiceConnection.addToQueue( it )
        }
        musicServiceConnection.moveSong( 0, 2 )
        assertEquals(
            testSongs.first().id,
            queueRepository.fetchSongsInQueueSortedByPosition().first()[2].id
        )
        assertEquals(2, connectable.player.currentMediaItemIndex)
        musicServiceConnection.moveSong( 2, 0 )
        assertEquals(0, connectable.player.currentMediaItemIndex)
        assertEquals(
            testSongs.size,
            queueRepository.fetchSongsInQueueSortedByPosition().first().size
        )
    }

    @Test
    fun testClearQueue() = runTest {
        queueRepository.sendSongs( emptyList() )
        testSongs.forEach {
            musicServiceConnection.addToQueue( it )
        }
        queueRepository.clearQueue()
        assertEquals( 0, connectable.player.mediaItemCount )

    }


    @Test
    fun testSetPlaybackSpeed() = runTest {
        queueRepository.sendSongs( emptyList() )
        setOf( .5f, 1f, 1.5f, 2f ).forEach {
            preferencesDataSource.setPlaybackSpeed( it )
            assertEquals( it, connectable.player.playbackParameters.speed )
        }
    }

    @Test
    fun testSetPlaybackPitch() = runTest {
        queueRepository.sendSongs( emptyList() )
        setOf( .5f, 1f, 1.5f, 2f ).forEach {
            preferencesDataSource.setPlaybackPitch( it )
            assertEquals(it, connectable.player.playbackParameters.pitch)
        }
    }

    @Test
    fun testSetRepeatMode() = runTest {
        queueRepository.sendSongs( emptyList() )
        assertEquals( Player.REPEAT_MODE_OFF, connectable.player.repeatMode )
        preferencesDataSource.setLoopMode( LoopMode.Song )
        assertEquals( Player.REPEAT_MODE_ONE, connectable.player.repeatMode )
    }

    @Test
    fun testPlayPreviousSong() = runTest {
        queueRepository.sendSongs( emptyList() )
        preferencesDataSource.sendUserData( emptyUserData )
        testSongs.forEach {
            musicServiceConnection.addToQueue( it )
        }
        assertEquals( 0, connectable.player.currentMediaItemIndex )

        musicServiceConnection.playPreviousSong()
        assertEquals( 0, connectable.player.currentMediaItemIndex )

        musicServiceConnection.playNextSong()
        musicServiceConnection.playNextSong()
        assertEquals( 2, connectable.player.currentMediaItemIndex )

        musicServiceConnection.playPreviousSong()
        assertEquals( 1, connectable.player.currentMediaItemIndex )

        musicServiceConnection.playPreviousSong()
        assertEquals( 0, connectable.player.currentMediaItemIndex )
    }

    @Test
    fun testWhenNowPlayingSongIsDeleted_theNextSongInQueueIsPlayed() = runTest {
        queueRepository.sendSongs( testSongs )
        mostPlayedSongsRepository.sendSongs( testSongs )
        playHistoryRepository.sendSongs( testSongs )
        songsAdditionalMetadataRepository.sendMetadata(
            testSongs.map {
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
        preferencesDataSource.sendUserData( emptyUserData )
        musicServiceConnection.playSong(
            song = testSongs.first(),
            songs = testSongs,
            shuffle = false
        )
        musicServiceConnection.deleteSong( testSongs.first() )
        assertEquals(
            testSongs[1].id,
            connectable.player.currentMediaItem?.mediaId
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