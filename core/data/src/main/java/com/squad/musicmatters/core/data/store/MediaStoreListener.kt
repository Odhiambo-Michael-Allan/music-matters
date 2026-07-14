package com.squad.musicmatters.core.data.store

interface MediaStoreListener {
    /**
     * Called by the [SongsStore] whenever the [android.provider.MediaStore] content changes.
     */
    fun onMediaStoreChanged()
}