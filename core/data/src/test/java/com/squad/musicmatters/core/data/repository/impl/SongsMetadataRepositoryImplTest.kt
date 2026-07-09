package com.squad.musicmatters.core.data.repository.impl

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.songs.MetadataStore
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.songs.testSong
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SongsMetadataRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testScope = TestScope( UnconfinedTestDispatcher() )

    private lateinit var metadataStore: FakeMetadataStore
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var subject: SongsMetadataRepository

    @Before
    fun setUp() {
        metadataStore = FakeMetadataStore()
        songsRepository = FakeSongsRepository()
    }

    @Test
    fun testFetchMetadata() = testScope.runTest {
        subject = SongsMetadataRepositoryImpl(
            metadataStore = metadataStore,
            songsRepository = songsRepository,
            coroutineScope = backgroundScope
        )
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        val metadata = listOf(
            SongMetadata(
                songId = "song-id-1",
                codec = "",
                bitrate = 0,
                bitsPerSample = 0,
                genre = "",
                samplingRate = 0f
            ),
            SongMetadata(
                songId = "song-id-2",
                codec = "",
                bitrate = 0,
                bitsPerSample = 0,
                genre = "",
                samplingRate = 0f
            ),
            SongMetadata(
                songId = "song-id-3",
                codec = "",
                bitrate = 0,
                bitsPerSample = 0,
                genre = "",
                samplingRate = 0f
            ),
        )
        metadataStore.sendMetadata( metadata )
        songsRepository.sendSongs( songs )
        assertEquals( 3, subject.fetchMetadata().first().size )
    }


}

private class FakeMetadataStore : MetadataStore {

    private var metadata = emptyList<SongMetadata>()

    override fun fetchMetadataFor( songs: List<Song> ): List<SongMetadata> = metadata

    fun sendMetadata( meta: List<SongMetadata> ) {
        this.metadata = meta
    }

}