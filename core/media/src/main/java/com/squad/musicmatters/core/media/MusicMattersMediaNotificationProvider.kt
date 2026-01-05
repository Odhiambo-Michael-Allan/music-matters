package com.squad.musicmatters.core.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.bumptech.glide.request.target.CustomTarget
import com.google.common.collect.ImmutableList
import com.squad.musicmatters.core.data.utils.VersionUtils
import com.squad.musicmatters.core.media.media.MusicService

import com.squad.musicMatters.core.i8n.R as i8nR

private const val MUSIC_MATTERS_PACKAGE_NAME = "com.squad.musicmatters"
private const val TARGET_ACTIVITY_NAME = "$MUSIC_MATTERS_PACKAGE_NAME.MainActivity"
private const val ACTION_QUIT = "$MUSIC_MATTERS_PACKAGE_NAME.quit_service"

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

@OptIn(UnstableApi::class)
class MusicMattersPlayingNotification(
    val context: MusicService,
    mediaSessionToken: MediaSessionCompat.Token
) : NotificationCompat.Builder( context, NOTIFICATION_CHANNEL_ID ){

    private var currentTarget: CustomTarget<Bitmap>? = null

    init {
        val clickIntent = context.openMainActivityPendingIntent()
        val quitAppIntent = context.createQuitAppIntent()
        val favoriteAction = buildFavoriteAction( false )
    }

    private fun buildFavoriteAction( isFavorite: Boolean ): NotificationCompat.Action {
        val favoriteResId = if ( isFavorite ) R.ic_liked else R.drawable.ic_liked_border 
    }

    companion object {

        internal const val NOTIFICATION_CHANNEL_ID = "playing_notification"
        const val NOTIFICATION_ID = 7546

        @RequiresApi( 26 )
        fun createNotificationChannel(
            context: Context,
            notificationManager: NotificationManager
        ) {
            var notificationChannel: NotificationChannel? = notificationManager
                .getNotificationChannel( NOTIFICATION_CHANNEL_ID )
            if ( notificationChannel == null ) {
                notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    context.getString( i8nR.string.core_i8n_playing_notification_name ),
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationChannel.description =
                    context.getString( i8nR.string.core_i8n_playing_notification_description )
                notificationChannel.enableLights( false )
                notificationChannel.enableVibration( false )
                notificationChannel.setShowBadge( false )

                notificationManager.createNotificationChannel( notificationChannel )
            }
        }

        fun from(
            context: MusicService,
            notificationManager: NotificationManager,
            mediaSession: MediaSessionCompat
        ): MusicMattersPlayingNotification {
            if ( VersionUtils.hasOreo() ) {
                createNotificationChannel( context, notificationManager )
            }
            return MusicMattersPlayingNotification( context, mediaSession.sessionToken )
        }
    }

}

private fun Context.openMainActivityPendingIntent(): PendingIntent? = PendingIntent.getActivity(
    this,
    0,
    Intent().apply {
        action = Intent.ACTION_VIEW
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

@OptIn( UnstableApi::class )
private fun Context.createQuitAppIntent(): PendingIntent? = PendingIntent.getService(
    this,
    0,
    Intent( ACTION_QUIT ).apply {
        component = ComponentName(
            this@createQuitAppIntent,
            MusicService::class.java
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

