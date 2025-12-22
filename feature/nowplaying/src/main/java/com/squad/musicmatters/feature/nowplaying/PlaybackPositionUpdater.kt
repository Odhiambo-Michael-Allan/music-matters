package com.squad.musicmatters.feature.nowplaying

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface PlaybackPositionUpdater {
    val playbackPosition: StateFlow<PlaybackPosition>
//    val totalDurationPreviousMediaItemPlayed: StateFlow<Long>

    fun startPeriodicUpdates()
    fun stopPeriodicUpdates()
    fun cleanUp()
}

class PlaybackPositionUpdaterImpl @Inject constructor(
    @Dispatcher( MusicMattersDispatchers.Main ) dispatcher: CoroutineDispatcher,
    private val player: MusicServiceConnection
) : PlaybackPositionUpdater {

    private val handler = Handler( Looper.getMainLooper() )
    private var periodicUpdatesStarted = false

    private var _playbackPosition = MutableStateFlow( player.getCurrentPlaybackPosition() )
    override val playbackPosition = _playbackPosition.asStateFlow()

    private val coroutineScope = CoroutineScope( dispatcher + SupervisorJob() )

    init {
        coroutineScope.launch {
            player.playerState.collect {
                if ( it.isPlaying ) startPeriodicUpdates()
                else stopPeriodicUpdates()
            }
        }
    }

    override fun startPeriodicUpdates() {
        if ( periodicUpdatesStarted ) return
        periodicUpdatesStarted = true
        update()
    }

    private fun update() {
        val currentPlaybackPosition = player.getCurrentPlaybackPosition()
        _playbackPosition.value = currentPlaybackPosition
        Log.d( TAG, "CURRENT PLAYBACK POSITION: $currentPlaybackPosition" )
        if ( periodicUpdatesStarted ) {
            handler.removeCallbacksAndMessages( null )
            handler.postDelayed( this::update, DEFAULT_PLAYBACK_POSITION_UPDATE_INTERVAL )
        }
    }


    override fun stopPeriodicUpdates() {
        periodicUpdatesStarted = false
//        /**
//         * The updates may be stopping because the player is transitioning to another media item.
//         * If that's the case, we need to report the total duration of playback of the previous
//         * media item and then fetch the playback position of the current media item if a
//         * transition occurred.
//         */
        _playbackPosition.value = player.getCurrentPlaybackPosition()
        handler.removeCallbacksAndMessages( null )
    }

    override fun cleanUp() {
        stopPeriodicUpdates()
        coroutineScope.cancel()
    }
}

private const val TAG = "PLAYBACK-POSITION-UPDATER"
const val DEFAULT_PLAYBACK_POSITION_UPDATE_INTERVAL = 1000L