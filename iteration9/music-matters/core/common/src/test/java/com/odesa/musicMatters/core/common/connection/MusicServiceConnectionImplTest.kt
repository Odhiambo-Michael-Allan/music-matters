package com.odesa.musicMatters.core.common.connection

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.odesa.musicMatters.core.data.preferences.allowedSpeedPitchValues
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.albums.testAlbumMediaItems
import com.odesa.musicMatters.core.datatesting.artists.testArtistMediaItems
import com.odesa.musicMatters.core.datatesting.connection.FakeConnectable
import com.odesa.musicMatters.core.datatesting.genres.testGenreMediaItems
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItems
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class MusicServiceConnectionImplTest {

    private val playbackPitch = 0.5f
    private val playbackSpeed = 0.5f
    private val currentRepeatMode = Player.REPEAT_MODE_OFF
    private lateinit var connectable: Connectable
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var musicServiceConnection: MusicServiceConnection


    @OptIn( ExperimentalCoroutinesApi::class )
    @Before
    fun setup() {
        connectable = FakeConnectable()
        playlistRepository = FakePlaylistRepository()
        settingsRepository = FakeSettingsRepository()
        musicServiceConnection = MusicServiceConnectionImpl(
            connectable,
            playlistRepository = playlistRepository,
            settingsRepository = settingsRepository,
            dispatcher = UnconfinedTestDispatcher(),
            initialPlaybackParameters = PlaybackParameters( playbackSpeed, playbackPitch ),
            initialRepeatMode = currentRepeatMode
        )
    }

    @Test
    fun testCachesAreCorrectlyUpdated() {
        TestCase.assertFalse(musicServiceConnection.isInitializing.value)
        TestCase.assertEquals(
            testSongMediaItems.size,
            musicServiceConnection.cachedSongs.value.size
        )
        TestCase.assertEquals(
            testGenreMediaItems.size,
            musicServiceConnection.cachedGenres.value.size
        )
        TestCase.assertEquals(
            3,
            musicServiceConnection.cachedGenres.value.find { it.name == "Pop" }!!.numberOfTracks
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            musicServiceConnection.cachedRecentlyAddedSongs.value.size
        )
        TestCase.assertEquals(
            testArtistMediaItems.size,
            musicServiceConnection.cachedArtists.value.size
        )
        TestCase.assertEquals(
            testArtistMediaItems.size,
            musicServiceConnection.cachedSuggestedArtists.value.size
        )
        TestCase.assertEquals(
            testAlbumMediaItems.size,
            musicServiceConnection.cachedAlbums.value.size
        )
        TestCase.assertEquals(
            testAlbumMediaItems.size,
            musicServiceConnection.cachedSuggestedAlbums.value.size
        )
    }

    @Test
    fun testAddToQueue() {
        testSongMediaItems.forEach {
            musicServiceConnection.addToQueue( it )
        }
        musicServiceConnection.addToQueue( testSongMediaItems.first() ) // NO DUPLICATES!
        TestCase.assertEquals(
            testSongMediaItems.size,
            musicServiceConnection.mediaItemsInQueue.value.size
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            connectable.player!!.mediaItemCount
        )
        TestCase.assertEquals(
            testSongMediaItems.first().mediaId,
            connectable.player!!.currentMediaItem!!.mediaId
        )
        TestCase.assertEquals(
            testSongMediaItems.first().mediaId,
            musicServiceConnection.nowPlayingMediaItem.value.mediaId
        )
        TestCase.assertEquals(
            0,
            musicServiceConnection.currentlyPlayingMediaItemIndex.value
        )
        TestCase.assertTrue(musicServiceConnection.playbackState.value.isPlaying)
        TestCase.assertEquals(
            0,
            connectable.player!!.currentMediaItemIndex
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testPlayNext() {
        testSongMediaItems.forEach {
            musicServiceConnection.playNext( it )
        }
        TestCase.assertEquals(
            testSongMediaItems.size,
            musicServiceConnection.mediaItemsInQueue.value.size
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            connectable.player!!.mediaItemCount
        )
        TestCase.assertTrue(musicServiceConnection.playbackState.value.isPlaying)
        TestCase.assertEquals(
            testSongMediaItems.first().mediaId,
            musicServiceConnection.mediaItemsInQueue.value.first().mediaId
        )
        musicServiceConnection.playNext(
            testSongMediaItems.last()
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            musicServiceConnection.mediaItemsInQueue.value.size
        )
        TestCase.assertEquals(
            testSongMediaItems.last().mediaId,
            musicServiceConnection.mediaItemsInQueue.value[1].mediaId
        )
        TestCase.assertEquals(
            testSongMediaItems.last().mediaId,
            connectable.player!!.getMediaItemAt(1).mediaId
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testShuffleAndPlay() {
        musicServiceConnection.shuffleAndPlay( testSongMediaItems )
        TestCase.assertTrue(musicServiceConnection.playbackState.value.isPlaying)
        TestCase.assertEquals(
            testSongMediaItems.size,
            musicServiceConnection.mediaItemsInQueue.value.size
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            connectable.player!!.mediaItemCount
        )
        TestCase.assertNotNull(
            musicServiceConnection.mediaItemsInQueue.value.find {
                it.mediaId == musicServiceConnection.nowPlayingMediaItem.value.mediaId
            }
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testSeekToNext() {
        testSongMediaItems.forEach {
            musicServiceConnection.addToQueue( it )
        }
        TestCase.assertEquals(0, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(0, connectable.player!!.currentMediaItemIndex)
        musicServiceConnection.playNextSong()
        TestCase.assertEquals(1, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(1, connectable.player!!.currentMediaItemIndex)
        musicServiceConnection.playNextSong()
        TestCase.assertEquals(2, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(2, connectable.player!!.currentMediaItemIndex)
    }

    @Test
    fun testShuffleSongsInQueue() {
        testSongMediaItems.forEach {
            musicServiceConnection.addToQueue( it )
        }
        connectable.player!!.seekToNext()  // testMediaItems[1] is currently playing
        musicServiceConnection.shuffleSongsInQueue()
        TestCase.assertEquals(0, connectable.player!!.currentMediaItemIndex)
        TestCase.assertEquals(0, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(
            testSongMediaItems[1].mediaId,
            musicServiceConnection.nowPlayingMediaItem.value.mediaId
        )
        TestCase.assertEquals(
            testSongMediaItems[1].mediaId,
            connectable.player!!.currentMediaItem!!.mediaId
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testMoveMediaItem() {
        testSongMediaItems.forEach {
            musicServiceConnection.addToQueue( it )
        }
        musicServiceConnection.moveMediaItem( 0, 2 )
        TestCase.assertEquals(
            testSongMediaItems.first().mediaId,
            musicServiceConnection.mediaItemsInQueue.value[2].mediaId
        )
        TestCase.assertEquals(2, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(2, connectable.player!!.currentMediaItemIndex)
        musicServiceConnection.moveMediaItem( 2, 0 )
        TestCase.assertEquals(0, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(0, connectable.player!!.currentMediaItemIndex)
        TestCase.assertEquals(
            testSongMediaItems.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testClearQueue() {
        testSongMediaItems.forEach {
            musicServiceConnection.addToQueue( it )
        }
        musicServiceConnection.clearQueue()
        TestCase.assertEquals(0, musicServiceConnection.mediaItemsInQueue.value.size)
        TestCase.assertEquals(0, connectable.player!!.mediaItemCount)
        TestCase.assertEquals(MediaItem.EMPTY, musicServiceConnection.nowPlayingMediaItem.value)
        TestCase.assertEquals(
            CURRENTLY_PLAYING_MEDIA_ITEM_INDEX_UNDEFINED,
            musicServiceConnection.currentlyPlayingMediaItemIndex.value
        )
        TestCase.assertTrue(
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.isEmpty()
        )
    }

    @Test
    fun testPlaybackParametersAreSetCorrectly() {
        TestCase.assertEquals(playbackSpeed, connectable.player!!.playbackParameters.speed)
        TestCase.assertEquals(playbackPitch, connectable.player!!.playbackParameters.pitch)
    }

    @Test
    fun testSetPlaybackSpeed() {
        allowedSpeedPitchValues.forEach {
            musicServiceConnection.setPlaybackSpeed( it )
            TestCase.assertEquals(it, connectable.player!!.playbackParameters.speed)
        }
    }

    @Test
    fun testSetPlaybackPitch() {
        allowedSpeedPitchValues.forEach {
            musicServiceConnection.setPlaybackPitch( it )
            TestCase.assertEquals(it, connectable.player!!.playbackParameters.pitch)
        }
    }

    @Test
    fun testSetRepeatMode() {
        TestCase.assertEquals(Player.REPEAT_MODE_OFF, connectable.player!!.repeatMode)
        listOf( Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ONE, Player.REPEAT_MODE_ALL ).forEach {
            musicServiceConnection.setRepeatMode( it )
            TestCase.assertEquals(it, connectable.player!!.repeatMode)
        }
    }

    @Test
    fun testPlayPause() {
        testSongMediaItems.forEach {
            musicServiceConnection.addToQueue( it )
        }
        musicServiceConnection.playPause()
        TestCase.assertFalse(musicServiceConnection.playbackState.value.isPlaying)
        TestCase.assertFalse(connectable.player!!.isPlaying)
        musicServiceConnection.playPause()
        TestCase.assertTrue(musicServiceConnection.playbackState.value.isPlaying)
        TestCase.assertTrue(connectable.player!!.isPlaying)
    }

    @Test
    fun testPlayPreviousSong() {
        testSongMediaItems.forEach {
            musicServiceConnection.addToQueue( it )
        }
        TestCase.assertEquals(0, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(0, connectable.player!!.currentMediaItemIndex)

        musicServiceConnection.playPreviousSong()
        TestCase.assertEquals(0, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(0, connectable.player!!.currentMediaItemIndex)

        musicServiceConnection.playNextSong()
        musicServiceConnection.playNextSong()
        TestCase.assertEquals(2, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(2, connectable.player!!.currentMediaItemIndex)

        musicServiceConnection.playPreviousSong()
        TestCase.assertEquals(1, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(1, connectable.player!!.currentMediaItemIndex)

        musicServiceConnection.playPreviousSong()
        TestCase.assertEquals(0, musicServiceConnection.currentlyPlayingMediaItemIndex.value)
        TestCase.assertEquals(0, connectable.player!!.currentMediaItemIndex)
        connectable.player
    }

    @Test
    fun testMediaItemsInQueueAreSavedCorrectly() {
        TestCase.assertTrue(playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.isEmpty())
        musicServiceConnection.playMediaItem(
            mediaItem = testSongMediaItems.first(),
            mediaItems = testSongMediaItems,
            shuffle = false
        )
        TestCase.assertEquals(
            testSongMediaItems.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }
}