package com.odesa.musicMatters.core.common.media.library

import com.odesa.musicMatters.core.datatesting.media.FakeMusicSource
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItems
import junit.framework.TestCase
import junit.framework.TestCase.fail
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
    fun testMediaItemIsDeletedCorrectly() {
        testSource.prepare()
        testSource.delete( testSongMediaItems.first().mediaId )
        testSource.forEach {
            if ( it.mediaId == testSongMediaItems.first().mediaId ) {
                fail( "MediaItem with id: ${testSongMediaItems.first().mediaId} should have been deleted" )
            }
        }
    }
}