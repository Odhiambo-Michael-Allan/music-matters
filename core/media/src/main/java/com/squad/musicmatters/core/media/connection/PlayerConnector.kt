package com.squad.musicmatters.core.media.connection

import androidx.media3.common.Player

interface PlayerConnector {
    val player: Player?
    suspend fun establishConnection()
//    suspend fun getChildren( parentId: String ): List<MediaItem>
//    suspend fun sendCustomCommand(
//        command: String,
//        parameters: Bundle?
//    ): Boolean
    fun addDisconnectListener( disconnectListener: () -> Unit )
}