package com.squad.musicmatters.ui

import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.data.settings.SettingsRepository
import com.squad.musicmatters.core.testing.connection.TestMusicServiceConnection
import com.squad.musicmatters.core.testing.playlist.FakePlaylistRepository
import com.squad.musicmatters.core.testing.playlists.testPlaylistInfos
import com.squad.musicmatters.core.model.PlaylistInfo
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class BaseViewModelTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var musicServiceConnection: TestMusicServiceConnection
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: BaseViewModel

    private lateinit var currentPlaylistInfos: List<PlaylistInfo>

    @Before
    fun setup() {
        settingsRepository = FakeSettingsRepository()
        musicServiceConnection = TestMusicServiceConnection()
        playlistRepository = FakePlaylistRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = BaseViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testPlaylistsAreUpdatedCorrectly() = runTest {
        viewModel.addOnPlaylistsChangeListener {
            currentPlaylistInfos = it
        }
        assertEquals( 1, currentPlaylistInfos.size )
        testPlaylistInfos.forEach {
            playlistRepository.savePlaylist( it )
        }
        assertEquals( 1 + testPlaylistInfos.size, currentPlaylistInfos.size )
    }

    @Test
    fun testPlaylistIsRenamedCorrectly() = runTest {
        testPlaylistInfos.forEach {
            playlistRepository.savePlaylist( it )
        }
        viewModel.addOnPlaylistsChangeListener {
            currentPlaylistInfos = it
        }
        viewModel.renamePlaylist( testPlaylistInfos.first(), "chill-rnb" )
        assertTrue( currentPlaylistInfos.map { it.title }.contains( "chill-rnb" ) )
    }

}