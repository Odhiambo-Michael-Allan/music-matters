package com.squad.musicmatters.glance

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.glance.data.GlanceRepository
import com.squad.musicmatters.glance.data.GlanceUiModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GlanceRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var playlistsRepository: FakePlaylistsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var subject: GlanceRepository

    @Before
    fun setUp() {
        userDataRepository = FakeUserDataRepository()
        songsRepository = FakeSongsRepository()
        player = FakeMusicMattersPlayer()
        playlistsRepository = FakePlaylistsRepository()
        subject = GlanceRepository(
            userDataRepository = userDataRepository,
            songsRepository = songsRepository,
            playlistsRepository = playlistsRepository,
            player = player,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun testGlanceUiModelIsFetchedCorrectly() = runTest {

        val songs = listOf(
            testSong( "1" ),
            testSong( id = "2" ),
            testSong( id = "3" ),
            testSong( id = "4" ),
        )

        userDataRepository.sendUserData(
            emptyUserData.copy(
                currentlyPlayingSongId = "2",
                loopMode = LoopMode.Queue,

            )
        )
        songsRepository.sendSongs( songs )
        player.sendPlayerState(
            PlayerState(
                currentlyPlayingSongId = "2",
                isPlaying = true,
            )
        )
        playlistsRepository.sendPlaylists( emptyList() )
        playlistsRepository.addToFavorites( testSong( id = "2" ) )

        assertEquals(
            GlanceUiModel(
                isPlaying = true,
                currentlyPlayingSong = testSong( id = "2" ),
                loopMode = LoopMode.Queue,
                shuffle = false,
                currentlyPlayingSongIsFavorite = true,
            ),
            subject.getGlanceUiModel().first()
        )
    }

}