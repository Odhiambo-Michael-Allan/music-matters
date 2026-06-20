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

//private const val MUSIC_MATTERS_PACKAGE_NAME = "com.squad.musicmatters"
//private const val TARGET_ACTIVITY_NAME = "$MUSIC_MATTERS_PACKAGE_NAME.MainActivity"
//private const val ACTION_QUIT = "$MUSIC_MATTERS_PACKAGE_NAME.quit_service"
//
//@UnstableApi
//class MusicMattersMediaNotificationProvider( private val context: Context) : MediaNotification.Provider {
//
//    override fun createNotification(
//        mediaSession: MediaSession,
//        customLayout: ImmutableList<CommandButton>,
//        actionFactory: MediaNotification.ActionFactory,
//        onNotificationChangedCallback: MediaNotification.Provider.Callback
//    ): MediaNotification {
//        val defaultMediaNotificationProvider = DefaultMediaNotificationProvider( context )
//        defaultMediaNotificationProvider.setSmallIcon( R.drawable.notification_icon )
//        return defaultMediaNotificationProvider
//            .createNotification(
//                mediaSession,
//                customLayout,
//                actionFactory,
//                onNotificationChangedCallback
//            )
//    }
//
//    override fun handleCustomCommand(
//        session: MediaSession,
//        action: String,
//        extras: Bundle
//    ): Boolean {
//        return false
//    }
//
//    override fun getNotificationChannelInfo(): MediaNotification.Provider.NotificationChannelInfo? {
//        TODO("Not yet implemented")
//    }
//}


