package com.odesa.musicMatters.core.common.media

import android.database.ContentObserver
import android.os.Handler

class MediaStoreObserver(
    private val handler: Handler,
    private val onContentChange: () -> Unit,
) : ContentObserver( handler ), Runnable {
    override fun onChange( selfChange: Boolean ) {
        // If a change is detected, remove any scheduled callback then
        // post a new one. This is intended to prevent closely spaced
        // events from generating multiple refresh calls.
        handler.removeCallbacks( this )
        handler.postDelayed( onContentChange, 500 )
    }

    override fun run() {
        // actually call refresh when the delayed callback fires.
        onContentChange()
    }
}