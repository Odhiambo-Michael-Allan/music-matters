package com.odesa.musicMatters.core.common.media.library

import com.odesa.musicMatters.core.common.media.extensions.toAlbum
import com.odesa.musicMatters.core.datatesting.media.FakeMusicSource
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItems
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class BrowseTreeTest {

    private lateinit var musicSource: MusicSource
    private lateinit var browseTree: BrowseTree

    @Before
    fun setup() {
        musicSource = FakeMusicSource( testSongMediaItems )
        browseTree = BrowseTree( musicSource )
    }

    @Test
    fun testTracksAreCorrectlyConfigured() {
        val trackList = browseTree[ MUSIC_MATTERS_TRACKS_ROOT ]
        TestCase.assertNotNull(trackList)
        assertEquals(11, trackList!!.size)
    }

    @Test
    fun testRecentlyAddedSongsAreCorrectlyConfigured() {
        val recentlyAddedSongs = browseTree[ MUSIC_MATTERS_RECENT_SONGS_ROOT ]
        TestCase.assertNotNull(recentlyAddedSongs)
        assertEquals(testSongMediaItems.size, recentlyAddedSongs!!.size)
        assertEquals("Don't Believe the Hype", recentlyAddedSongs[0].mediaMetadata.title)
    }

    @Test
    fun testAlbumsAreLoadedCorrectly() {
        val albums = browseTree[ MUSIC_MATTERS_ALBUMS_ROOT ]
        TestCase.assertNotNull(albums)
        assertEquals(5, albums!!.size)
        TestCase.assertTrue(albums.first().toAlbum(emptyList()).artists.isNotEmpty())
    }

    @Test
    fun testSuggestedAlbumAreLoadedCorrectly() {
        val suggestedAlbums = browseTree[ MUSIC_MATTERS_SUGGESTED_ALBUMS_ROOT ]
        TestCase.assertNotNull(suggestedAlbums)
        assertEquals(5, suggestedAlbums!!.size)
    }

    @Test
    fun testArtistsAreLoadedCorrectly() {
        val artists = browseTree[ MUSIC_MATTERS_ARTISTS_ROOT ]
        TestCase.assertNotNull(artists)
        assertEquals(5, artists!!.size)
    }

    @Test
    fun testSuggestedArtistsAreLoadedCorrectly() {
        val suggestedArtists = browseTree[ MUSIC_MATTERS_SUGGESTED_ARTISTS_ROOT ]
        TestCase.assertNotNull(suggestedArtists)
        assertEquals(5, suggestedArtists!!.size)
    }

    @Test
    fun testSongIsDeletedCorrectly() {
        browseTree.deleteMediaItemWithId( testSongMediaItems.first().mediaId )
        assertEquals(
            testSongMediaItems.size - 1,
            browseTree[ MUSIC_MATTERS_TRACKS_ROOT ]!!.size
        )
    }
}