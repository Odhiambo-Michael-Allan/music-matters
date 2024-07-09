package com.odesa.musicMatters.core.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.odesa.musicMatters.core.data.database.CURRENT_PLAYING_QUEUE_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.FAVORITES_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.FAVORITES_PLAYLIST_TITLE
import com.odesa.musicMatters.core.data.database.MusicMattersDatabase
import com.odesa.musicMatters.core.data.database.RECENTLY_PLAYED_SONGS_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.RECENTLY_PLAYED_SONGS_PLAYLIST_TITLE
import com.odesa.musicMatters.core.data.database.model.Playlist
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
abstract class TestDatabase {
    lateinit var database: MusicMattersDatabase

    @Before
    fun initializeDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = MusicMattersDatabase::class.java
        ).allowMainThreadQueries().build()
        database.playlistDao().apply {
            runBlocking {
                insertAll(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = FAVORITES_PLAYLIST_TITLE
                    ),
                    Playlist(
                        id = RECENTLY_PLAYED_SONGS_PLAYLIST_ID,
                        title = RECENTLY_PLAYED_SONGS_PLAYLIST_TITLE
                    ),
                    Playlist(
                        id = CURRENT_PLAYING_QUEUE_PLAYLIST_ID,
                        title = ""
                    )
                )
            }
        }
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}