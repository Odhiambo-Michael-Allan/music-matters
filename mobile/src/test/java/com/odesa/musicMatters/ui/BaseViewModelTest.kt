package com.odesa.musicMatters.ui

import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.connection.FakeMusicServiceConnection
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistRepository
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.model.PlaylistInfo
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
    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: BaseViewModel

    private lateinit var currentPlaylistInfos: List<PlaylistInfo>

    @Before
    fun setup() {
        settingsRepository = FakeSettingsRepository()
        musicServiceConnection = FakeMusicServiceConnection()
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