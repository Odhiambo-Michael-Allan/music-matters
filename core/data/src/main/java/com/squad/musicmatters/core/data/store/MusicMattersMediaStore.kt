package com.squad.musicmatters.core.data.store

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

open class MusicMattersMediaStore(
    context: Context,
    ioScope: CoroutineScope,
) {

    private val listeners = mutableSetOf<MediaStoreListener>()

    private val mediaStoreObserverHandlerThread: HandlerThread = HandlerThread(
        "MediaStoreObserverHandlerThread"
    )
    private val mediaStoreObserver: MediaStoreObserver

    // A flag to check if the store has been initialized/released.
    private var mediaStoreObserverIsRegistered = false
    protected val contentResolver: ContentResolver = context.contentResolver
    protected val collectionUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    init {
        // Start the thread and initialize the observer on a background looper
        mediaStoreObserverHandlerThread.start()
        mediaStoreObserver = MediaStoreObserver(
            Handler( mediaStoreObserverHandlerThread.looper )
        )
        contentResolver.registerContentObserver(
            collectionUri,
            true,
            mediaStoreObserver
        )
        mediaStoreObserverIsRegistered = true
        ioScope.launch {
            MediaPermissionsManager.hasAllRequiredPermissions.collect { granted ->
                if ( granted ) { listeners.forEach { it.onMediaStoreChanged() } }
            }
        }
    }

    fun registerListener( listener: MediaStoreListener ) {
        Timber.tag( TAG ).d( "REGISTERING LISTENER" )
        listeners.add( listener )
    }

    fun unregisterListener( listener: MediaStoreListener ) {
        Timber.tag( TAG ).d( "UNREGISTERING LISTENER" )
        listeners.remove( listener )
    }

    /**
     * Public method to clean up the ContentObserver and its HandlerThread. This MUST be called
     * by the hosting service (MediaLibraryService) in the onDestroy() method.
     */
    fun release() {
        if ( mediaStoreObserverIsRegistered ) {
            contentResolver.unregisterContentObserver( mediaStoreObserver )
            mediaStoreObserverHandlerThread.quitSafely()
            listeners.clear()
            mediaStoreObserverIsRegistered = false
        }
    }



    // A custom ContentObserver.
    private inner class MediaStoreObserver(
        private val handler: Handler
    ) : ContentObserver( handler ), Runnable {

        override fun onChange( selfChange: Boolean ) {
            // Debouncing logic: remove previously scheduled callback, then post new delayed
            // callback.
            handler.removeCallbacks( this )
            handler.postDelayed( this, 500 )
        }

        override fun run() {
            listeners.forEach { it.onMediaStoreChanged() }
        }
    }
}

private const val TAG = "MM-MEDIA-STORE"