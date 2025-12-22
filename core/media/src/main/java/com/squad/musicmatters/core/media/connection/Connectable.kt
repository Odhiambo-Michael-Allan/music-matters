package com.squad.musicmatters.core.media.connection

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player

interface Connectable {
    val player: Player?
    suspend fun establishConnection()
    suspend fun getChildren( parentId: String ): List<MediaItem>
    suspend fun sendCustomCommand(
        command: String,
        parameters: Bundle?
    ): Boolean
    fun addDisconnectListener( disconnectListener: () -> Unit )
}