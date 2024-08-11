package com.odesa.musicMatters.core.common

import android.content.Context
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList

@UnstableApi
class MusicMattersMediaNotificationProvider( private val context: Context) : MediaNotification.Provider {

    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {
        val defaultMediaNotificationProvider = DefaultMediaNotificationProvider( context )
        defaultMediaNotificationProvider.setSmallIcon( R.drawable.notification_icon )
        return defaultMediaNotificationProvider
            .createNotification(
                mediaSession,
                customLayout,
                actionFactory,
                onNotificationChangedCallback
            )
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle
    ): Boolean {
        return false
    }
}