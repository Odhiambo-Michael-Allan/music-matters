package com.odesa.musicMatters.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.odesa.musicMatters.core.model.SearchFilter

internal fun displayToastWithMessage(context: Context, message: String ) = Toast.makeText(
    context,
    message,
    Toast.LENGTH_SHORT
).show()

internal fun shareSong(context: Context, uri: Uri, localizedErrorMessage: String ) {
    try {
        val intent = createShareSongIntent( context, uri )
        context.startActivity( intent )
    } catch ( exception: Exception ) {
        displayToastWithMessage(
            context,
            localizedErrorMessage
        )
    }
}

internal fun createShareSongIntent(context: Context, uri: Uri) = Intent( Intent.ACTION_SEND ).apply {
    addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION )
    putExtra( Intent.EXTRA_STREAM, uri )
    type = context.contentResolver.getType( uri )
}

internal fun getSearchFilterFrom( name: String ) =
    when ( name ) {
        SearchFilter.SONG.name -> SearchFilter.SONG
        SearchFilter.ALBUM.name -> SearchFilter.ALBUM
        SearchFilter.ARTIST.name -> SearchFilter.ARTIST
        SearchFilter.GENRE.name -> SearchFilter.GENRE
        SearchFilter.PLAYLIST.name -> SearchFilter.PLAYLIST
        else -> null
    }
