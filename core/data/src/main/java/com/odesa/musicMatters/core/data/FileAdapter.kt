package com.odesa.musicMatters.core.data

import timber.log.Timber
import java.io.File

/**
 * An adapter that is used to read and write from the app's local cache.
 */
class FileAdapter( private val file: File) {
    fun overwrite( content: String ) = overwrite( content.toByteArray() )
    private fun overwrite( bytes: ByteArray ) {
        file.outputStream().use {
            it.write( bytes )
        }
    }
    fun read(): String {
        val contentRead = file.inputStream().use { String( it.readBytes() ) }
        Timber.tag( TAG ).d( "CONTENT READ BY FILE ADAPTER: $contentRead" )
        return contentRead
    }
}

private const val TAG = "FILE ADAPTER TAG"