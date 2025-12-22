package com.squad.musicmatters.core.datastore

import androidx.datastore.core.CorruptionException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class UserPreferencesSerializerTest {

    private val subject = UserPreferencesSerializer()

    @Test
    fun defaultUserPreferences_isEmpty() {
        assertEquals(
            userPreferences {},
            subject.defaultValue
        )
    }

    @Test
    fun writingAndReadingUserPreferences_outputsCorrectValue() = runTest {
        val expectedUserPreferences = userPreferences {
            shuffle = true
            pauseOnHeadphonesDisconnect = true
        }

        val outputStream = ByteArrayOutputStream()
        expectedUserPreferences.writeTo( outputStream )

        val inputStream = ByteArrayInputStream( outputStream.toByteArray() )
        val actualPreferences = subject.readFrom( inputStream )

        assertEquals(
            expectedUserPreferences,
            actualPreferences
        )
    }

    @Test( expected = CorruptionException::class )
    fun readingInvalidUserPreferences_throwsCorruptionException() = runTest {
        subject.readFrom( ByteArrayInputStream( byteArrayOf( 0 ) ) )
    }

}