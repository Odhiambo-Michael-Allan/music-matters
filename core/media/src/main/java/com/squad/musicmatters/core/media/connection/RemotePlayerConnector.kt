package com.squad.musicmatters.core.media.connection

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.guava.await
import timber.log.Timber

class RemotePlayerConnector(
    private val context: Context,
    private val serviceComponentName: ComponentName
) : PlayerConnector {

    private var playerController: MediaController? = null
    private val disconnectListeners: MutableList<() -> Unit> = mutableListOf()

    override val player: Player?
        get() = playerController

    override suspend fun establishConnection() {
        if ( playerController?.isConnected == true ) return

        Timber.tag( TAG ).d( "ESTABLISHING CONNECTION TO REMOTE PLAYER" )
        try {
            val token = SessionToken( context, serviceComponentName )

            // The media controller that is used to communicate with the media session.
            playerController = MediaController.Builder( context, token )
                .setListener( ControllerListener() )
                .buildAsync()
                .await()

        } catch ( e: Exception ) {
            Timber.tag( TAG ).e( e, "Controller connection failed" )
            throw e
        }
    }

    override fun addDisconnectListener( disconnectListener: () -> Unit ) {
        disconnectListeners.add( disconnectListener )
    }

    private inner class ControllerListener : MediaController.Listener {

        override fun onDisconnected( controller: MediaController ) {
            disconnectListeners.forEach {
                it.invoke()
            }
        }

    }
}

private const val TAG = "-MEDIA-BROWSER-ADAPTER-"