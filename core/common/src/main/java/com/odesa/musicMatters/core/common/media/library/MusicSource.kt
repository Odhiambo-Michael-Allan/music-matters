package com.odesa.musicMatters.core.common.media.library

import androidx.annotation.IntDef
import androidx.media3.common.MediaItem
import timber.log.Timber

/**
 * Interface used by [MusicService] for looking up [MediaMetadataCompat] objects.
 * Because Kotlin provides methods such as [Iterable.find] and [Iterable.filter], this is a
 * convenient interface to have on sources.
 */
interface MusicSource : Iterable<MediaItem> {

    /**
     * Begins loading the data for this music source
     */
    suspend fun load()

    /**
     * Method will perform a given action after this [MusicSource] is ready to be used.
     *
     * @param performAction A lambda expression to be called with a boolean parameter when the
     * source is ready. 'true' indicates the source was successfully prepared. 'false' indicates
     * an error occurred.
     */
    fun whenReady( performAction: ( Boolean ) -> Unit ): Boolean

    fun delete( mediaItemId: String )
}

@IntDef(
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
)
@Retention( AnnotationRetention.RUNTIME )
annotation class State

/**
 * State indicating the source was created, but no initialization has performed
 */
const val STATE_CREATED = 1

/**
 * State indicating initialization of the source is in progress
 */
const val STATE_INITIALIZING = 2

/**
 * State indicating the source has been initialized and is ready to be used
 */
const val STATE_INITIALIZED = 3

/**
 * State indicating an error has occurred.
 */
const val STATE_ERROR = 4

/**
 * Base class for music sources in Musically.
 */
abstract class AbstractMusicSource : MusicSource {

    @State
    var state: Int = STATE_CREATED
        set( value ) {
            if ( value == STATE_INITIALIZED || value == STATE_ERROR ) {
                synchronized( onReadyListeners ) {
                    Timber.tag( TAG ).d( "STATE CHANGED, NOTIFYING LISTENERS" )
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener( state == STATE_INITIALIZED )
                    }
                }
            } else
                field = value
        }

    private val onReadyListeners = mutableListOf<( Boolean ) -> Unit>()

    /**
     * Performs an action when this MusicSource is ready. This method is *not* threadsafe. Ensure
     * actions and state changes are only performed on a single thread.
     */
    override fun whenReady( performAction: ( Boolean ) -> Unit ): Boolean {
        return when ( state ) {
            STATE_CREATED, STATE_INITIALIZING -> {
                Timber.tag( TAG ).d( "ADDING ACTION TO BE PERFORMED LATER - whenReady" )
                onReadyListeners += performAction
                false
            }

            else -> {
                Timber.tag( TAG ).d( "PERFORMING ACTION IN - whenReady" )
                performAction( state != STATE_ERROR )
                true
            }
        }
    }

}

private const val TAG = "MUSIC-SOURCE"