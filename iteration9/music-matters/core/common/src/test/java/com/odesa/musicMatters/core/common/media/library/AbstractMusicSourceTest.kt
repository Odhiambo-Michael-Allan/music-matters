package com.odesa.musicMatters.core.common.media.library

import android.os.Bundle
import android.provider.MediaStore
import com.odesa.musicMatters.core.datatesting.media.FakeMusicSource
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItems
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * A set of Android integration tests for ( primarily ) [AbstractMusicSource]. The tests all use an
 * extension of [AbstractMusicSource] which is defined at the bottom of this file: [FakeMusicSource]
 */
@RunWith( RobolectricTestRunner::class )
class AbstractMusicSourceTest {

    private lateinit var testSource: FakeMusicSource

    @Before
    fun setup() {
        testSource = FakeMusicSource( testSongMediaItems )
    }


    @Test
    fun testWhenSourceSuccessfullyInitializes_listenersAreNotified() {
        var waiting = true
        testSource.whenReady {
            TestCase.assertTrue(it)
            waiting = false
        }
        TestCase.assertTrue(waiting)
        testSource.prepare()
        TestCase.assertFalse(waiting)
    }

    @Test
    fun testWhenErrorOccursWhileInitializing_listenersAreNotified() {
        var waiting = true
        testSource.whenReady {
            TestCase.assertFalse(it)
            waiting = false
        }
        TestCase.assertTrue(waiting)
        testSource.error()
        TestCase.assertFalse(waiting)
    }

    @Test
    fun testSearchByGenre() {
        testSource.prepare()
        val searchQuery = "Rock"
        val searchExtras = Bundle().apply {
            putString( MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE )
            putString( MediaStore.EXTRA_MEDIA_GENRE, searchQuery )
        }
        val result = testSource.search( searchQuery, searchExtras )
        TestCase.assertEquals(3, result.size)
    }

    @Test
    fun testSearchByMedia() {
        testSource.prepare()
        val searchQuery = "About a Guy"
        val searchExtras = Bundle().apply {
            putString( MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Media.ENTRY_CONTENT_TYPE )
            putString( MediaStore.EXTRA_MEDIA_TITLE, searchQuery )
            putString( MediaStore.EXTRA_MEDIA_ALBUM, "Tales from the Render Farm" )
            putString( MediaStore.EXTRA_MEDIA_ARTIST, "7 Developers and a Pastry Chef" )
        }
        val result = testSource.search( searchQuery, searchExtras )
        TestCase.assertEquals(1, result.size)
    }

    @Test
    fun testSearchByMedia_noMatches() {
        testSource.prepare()
        val searchQuery = "Kotlin in 31 Days"
        val searchExtras = Bundle().apply {
            putString( MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Media.ENTRY_CONTENT_TYPE )
            putString( MediaStore.EXTRA_MEDIA_TITLE, searchQuery )
            putString( MediaStore.EXTRA_MEDIA_ALBUM, "Delegated by lazy" )
            putString( MediaStore.EXTRA_MEDIA_ARTIST, "Brainiest Jet" )
        }
        val result = testSource.search( searchQuery, searchExtras )
        TestCase.assertEquals(0, result.size)
    }

    @Test
    fun testSearchByKeyword_fallback() {
        testSource.prepare()
        val searchQuery = "hasse"
        val searchExtras = Bundle.EMPTY
        val result = testSource.search( searchQuery, searchExtras )
        TestCase.assertEquals(1, result.size)
    }
}