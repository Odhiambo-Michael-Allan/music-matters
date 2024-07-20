package com.odesa.musicMatters.core.common.connection

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.guava.await
import timber.log.Timber

class MediaBrowserAdapter(
    private val context: Context,
    private val serviceComponentName: ComponentName
) : Connectable {

    private var browser: MediaBrowser? = null
    private val disconnectListeners: MutableList<() -> Unit> = mutableListOf()

    override val player: Player?
        get() = browser

    override suspend fun establishConnection() {
        val newBrowser =
            MediaBrowser.Builder( context, SessionToken( context, serviceComponentName ) )
                .setListener( BrowserListener() )
                .buildAsync()
                .await()
        browser = newBrowser
        newBrowser.getLibraryRoot( /* params = */ null ).await().value
    }

    override suspend fun getChildren( parentId: String ): List<MediaItem> {
        val children = this.browser?.getChildren( parentId, 0, Int.MAX_VALUE, null )
            ?.await()?.value
        return children ?: ImmutableList.of()
    }

    override suspend fun sendCustomCommand(
        command: String,
        parameters: Bundle?,
    ): Boolean = if ( browser?.isConnected == true ) {
        Timber.tag( TAG ).d( "SENDING CUSTOM COMMAND: $command" )
        val args = parameters ?: Bundle()
        browser?.sendCustomCommand( SessionCommand( command, args ), args )?.await()
        true
    } else false

    override fun addDisconnectListener( disconnectListener: () -> Unit ) {
        disconnectListeners.add( disconnectListener )
    }

    private inner class BrowserListener : MediaBrowser.Listener {
        override fun onDisconnected( controller: MediaController ) {
            disconnectListeners.forEach {
                it.invoke()
            }
        }
    }
}

private const val TAG = "--MEDIA-BROWSER-ADAPTER-TAG--"