package com.squad.musicmatters.core.data.songs

interface SongsStoreListener {
    /**
     * Called by the [SongsStore] whenever the [android.provider.MediaStore] content changes.
     */
    fun onMediaStoreChanged()
}