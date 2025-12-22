package com.squad.musicmatters.core.testing.connection

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.testing.media.FakePlayer
import com.squad.musicmatters.core.media.connection.Connectable

@UnstableApi
class FakeConnectable : Connectable {

    private val _player = FakePlayer()
    private val onDisconnectListeners: MutableList<()-> Unit> = mutableListOf()
    override val player = _player

    override suspend fun establishConnection() {}

    override suspend fun getChildren( parentId: String ): List<MediaItem> {
        return emptyList()
    }

    override suspend fun sendCustomCommand(
        command: String,
        parameters: Bundle?,
    ): Boolean = true

    override fun addDisconnectListener( disconnectListener: () -> Unit ) {
        onDisconnectListeners.add( disconnectListener )
    }

}