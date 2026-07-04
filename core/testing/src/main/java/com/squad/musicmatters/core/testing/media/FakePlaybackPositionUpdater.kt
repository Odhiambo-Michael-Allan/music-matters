package com.squad.musicmatters.core.testing.media

import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.media.PlaybackPositionUpdater
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePlaybackPositionUpdater : PlaybackPositionUpdater {
    private val _playbackPosition = MutableStateFlow( PlaybackPosition.ZERO )
    override val playbackPosition = _playbackPosition.asStateFlow()

    override fun startPeriodicUpdates() {
        TODO("Not yet implemented")
    }

    override fun stopPeriodicUpdates() {
        TODO("Not yet implemented")
    }

    override fun cleanUp() {
        TODO("Not yet implemented")
    }

    fun setPlaybackPosition( playbackPosition: PlaybackPosition ) {
        _playbackPosition.tryEmit( playbackPosition )
    }
}