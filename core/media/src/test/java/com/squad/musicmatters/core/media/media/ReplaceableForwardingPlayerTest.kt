package com.squad.musicmatters.core.media.media

import androidx.media3.common.MediaItem
import com.squad.musicmatters.core.media.connection.SongToMediaItemConverter
import com.squad.musicmatters.core.media.connection.testSongs
import com.squad.musicmatters.core.media.media.extensions.getMediaItems
import com.squad.musicmatters.core.model.QueueEntry
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.testing.media.FakePlayer
import com.squad.musicmatters.core.testing.repository.FakeQueueRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ReplaceableForwardingPlayerTest {

    private lateinit var player: FakePlayer
    private lateinit var queueRepository: FakeQueueRepository
    private lateinit var userPreferencesDataSource: FakeUserDataRepository
    private lateinit var subject: ReplaceableForwardingPlayer

    @Before
    fun setUp() {
        player = FakePlayer()
        queueRepository = FakeQueueRepository()
        userPreferencesDataSource = FakeUserDataRepository()
        subject = ReplaceableForwardingPlayer(
            player = player,
            queueRepository = queueRepository,
            coroutineScope = TestScope(),
            userPreferences = userPreferencesDataSource,
            songToMediaItemConverter = object : SongToMediaItemConverter {
                override fun convert( song: Song ): MediaItem {
                    return song.toTestMediaItem()
                }
            })
    }

    @Test
    fun whenShuffleModeEnabledIsTrue_playerIsInitializedWithCurrentPlayingQueue() = runTest {
        val queueEntries = listOf(
            QueueEntry(
                songId = testSongs[0].id,
                currentPositionInQueue = 4,
                originalPositionInQueue = 0
            ),
            QueueEntry(
                songId = testSongs[1].id,
                currentPositionInQueue = 3,
                originalPositionInQueue = 1,
            ),
            QueueEntry(
                songId = testSongs[2].id,
                currentPositionInQueue = 2,
                originalPositionInQueue = 2,
            ),
            QueueEntry(
                songId = testSongs[3].id,
                currentPositionInQueue = 1,
                originalPositionInQueue = 3,
            ),
            QueueEntry(
                songId = testSongs[4].id,
                currentPositionInQueue = 0,
                originalPositionInQueue = 4,
            )
        )
        queueRepository.saveQueue( queueEntries )
        queueRepository.sendSongs( testSongs )
        userPreferencesDataSource.sendUserData(
            emptyUserData.copy(
                currentlyPlayingSongId = testSongs.last().id,
                shuffle = true
            )
        )
        subject.initialize()
        assertEquals( testSongs.size, player.mediaItemCount )
        assertEquals( 0, player.currentMediaItemIndex )
        assertEquals( testSongs[4].id, player.getMediaItems().first().mediaId )
        assertEquals(
            testSongs.first().id,
            player.getMediaItems().last().mediaId
        )
    }

    @Test
    fun whenShuffleModeEnabledIsFalse_playerIsInitializedWithOriginalPlayingQueue() = runTest {
        val queueEntries = listOf(
            QueueEntry(
                songId = testSongs[0].id,
                currentPositionInQueue = 4,
                originalPositionInQueue = 0
            ),
            QueueEntry(
                songId = testSongs[1].id,
                currentPositionInQueue = 3,
                originalPositionInQueue = 1,
            ),
            QueueEntry(
                songId = testSongs[2].id,
                currentPositionInQueue = 2,
                originalPositionInQueue = 2,
            ),
            QueueEntry(
                songId = testSongs[3].id,
                currentPositionInQueue = 1,
                originalPositionInQueue = 3,
            ),
            QueueEntry(
                songId = testSongs[4].id,
                currentPositionInQueue = 0,
                originalPositionInQueue = 4,
            )
        )
        queueRepository.saveQueue( queueEntries )
        queueRepository.sendSongs( testSongs )
        userPreferencesDataSource.sendUserData(
            emptyUserData.copy(
                currentlyPlayingSongId = testSongs.last().id,
                shuffle = false,
            ),
        )
        subject.initialize()
        assertEquals( testSongs.size, player.mediaItemCount )
        assertEquals( 4, player.currentMediaItemIndex )
        assertEquals( testSongs[0].id, player.getMediaItems().first().mediaId )
        assertEquals(
            testSongs.last().id,
            player.getMediaItems().last().mediaId
        )
    }

    @Test
    fun testShuffleSongsInQueue() {
        testSongs.forEach {
            subject.addMediaItem( it.toTestMediaItem() )
        }
        // At this point, testSongs[0] is currently playing
        TestCase.assertEquals(
            testSongs.first().id,
            player.currentMediaItem!!.mediaId
        )
        subject.seekToNext()
        // testSongs[1] is currently playing
        TestCase.assertEquals(
            testSongs[1].id,
            player.currentMediaItem?.mediaId
        )
        subject.shuffleModeEnabled = true
        // The previously playing song [testSongs[1]] should have been moved to the
        // first position after shuffle.
        TestCase.assertEquals(
            testSongs[1].id,
            player.getMediaItems().first().mediaId
        )
        TestCase.assertEquals(
            testSongs.size,
            player.mediaItemCount
        )

        subject.shuffleModeEnabled = false
        assertEquals(
            testSongs.first().id,
            player.getMediaItems().first().mediaId
        )
        assertEquals(
            testSongs.last().id,
            player.getMediaItems().last().mediaId
        )
    }

}

private fun Song.toTestMediaItem() = MediaItem.Builder().setMediaId( id ).build()