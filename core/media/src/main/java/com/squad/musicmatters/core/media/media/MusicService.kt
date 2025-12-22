package com.squad.musicmatters.core.media.media

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.squad.musicmatters.core.common.di.ApplicationScope
import com.squad.musicmatters.core.data.repository.MostPlayedSongsRepository
import com.squad.musicmatters.core.data.repository.PlayHistoryRepository
import com.squad.musicmatters.core.media.MusicMattersMediaNotificationProvider
import com.squad.musicmatters.core.media.R
import com.squad.musicmatters.core.media.media.library.MEDIA_SEARCH_SUPPORTED
import com.squad.musicmatters.core.media.media.library.MUSIC_MATTERS_BROWSABLE_ROOT
import com.squad.musicmatters.core.data.songs.MediaPermissionsManager
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.impl.SongsStoreImpl
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

/**
 * Service for browsing the catalogue and receiving  a [MediaController] from the app's UI
 * and other apps that will to play music via MUSICALLY.
 *
 * Browsing begins with the method [MusicService.MusicServiceCallback.onGetLibraryRoot], and
 * continues in the callback [MusicService.MusicServiceCallback.onGetChildren].
 *
 */

@AndroidEntryPoint
@UnstableApi
class MusicService : MediaLibraryService() {

    @Inject
    @ApplicationScope
    lateinit var serviceScope: CoroutineScope

    @Inject
    lateinit var userPreferencesDataSource: PreferencesDataSource

    @Inject
    lateinit var songsStore: SongsStore

    @Inject
    lateinit var mostPlayedSongsRepository: MostPlayedSongsRepository

    @Inject
    lateinit var playHistoryRepository: PlayHistoryRepository

    private lateinit var mediaSession: MediaLibrarySession
    private val headsetReceiverIntentFilter = IntentFilter( Intent.ACTION_HEADSET_PLUG )

    private val catalogueRootMediaItem: MediaItem by lazy {
        MediaItem.Builder()
            .setMediaId( MUSIC_MATTERS_BROWSABLE_ROOT )
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setFolderType( MediaMetadata.FOLDER_TYPE_ALBUMS )
                    .setIsPlayable( false )
                    .build() )
            .build()
    }

    private val musicallyAudioAttributes = AudioAttributes.Builder()
        .setContentType( C.AUDIO_CONTENT_TYPE_MUSIC )
        .setUsage( C.USAGE_MEDIA )
        .build()

    private val playerListener = PlayerEventListener()

    /**
     * Configure ExoPlayer to handle audio focus for use. See [ExoPlayer.Builder.setAudioAttributes]
     * for details.
     */
    private val exoPlayer: Player by lazy {
        val player = ExoPlayer.Builder( this ).build().apply {
            setAudioAttributes( musicallyAudioAttributes, true )
            setHandleAudioBecomingNoisy( true )
            addListener( playerListener )
        }
        player.addAnalyticsListener( EventLogger(  "exoplayer-music-matters" ) )
        player
    }

    private val replaceableForwardingPlayer: ReplaceableForwardingPlayer by lazy {
        ReplaceableForwardingPlayer( exoPlayer )
    }

    private val sessionCommands =
        MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
            .also { builder ->
                builder.add( SessionCommand( CUSTOM_COMMAND_DELETE_SONG, Bundle.EMPTY ) )
            }.build()

    /** @return the {@link MediaLibrarySessionCallback} to be used to build the media session */
    @OptIn(UnstableApi::class)
    fun getCallback(): MediaLibrarySession.Callback {
        return MusicServiceCallback()
    }

    private val headsetReceiver = object : BroadcastReceiver() {
        override fun onReceive( context: Context?, intent: Intent? ) {
            intent?.action?.let { action ->
                if ( action == Intent.ACTION_HEADSET_PLUG ) {
                    when ( intent.getIntExtra( "state", -1 ) ) {
                        0 -> {
                            Timber.tag( TAG ).d( "HEADSET DISCONNECTED" )
                            serviceScope.launch {
                                if (
                                    userPreferencesDataSource
                                        .userData
                                        .map { it.pauseOnHeadphonesDisconnect }
                                        .first()
                                ) {
                                    exoPlayer.pause()
                                }
                            }
                        }
                        1 -> {
                            Timber.tag( TAG ).d( "HEADSET CONNECTED" )
                            serviceScope.launch {
                                if (
                                    userPreferencesDataSource
                                        .userData
                                        .map { it.playOnHeadphonesConnect }
                                        .first()
                                ) {
                                    exoPlayer.play()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        mediaSession = with (
            MediaLibrarySession.Builder(this, replaceableForwardingPlayer, getCallback() )
        ) {
            setId( packageName )
            packageManager?.getLaunchIntentForPackage( packageName )?.let { sessionIntent ->
                setSessionActivity(
                    PendingIntent.getActivity(
                        this@MusicService,
                        0,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
            build()
        }
        MediaPermissionsManager.checkForPermissions( applicationContext )
        setMediaNotificationProvider( MusicMattersMediaNotificationProvider( applicationContext ) )
        registerHeadsetEvents()

    }

    private fun registerHeadsetEvents() {
        registerReceiver( headsetReceiver, headsetReceiverIntentFilter )
    }

    override fun onGetSession( controllerInfo: MediaSession.ControllerInfo ): MediaLibrarySession? {
        return mediaSession
    }

    /**
     * Called when swiping the activity away from recents
     */
    override fun onTaskRemoved( rootIntent: Intent? ) {
        super.onTaskRemoved( rootIntent )
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaSession()
        unregisterReceiver( headsetReceiver )
        ( songsStore as? SongsStoreImpl )?.release()
    }

    private fun releaseMediaSession() {
        mediaSession.run {
            release()
            if ( player.playbackState != Player.STATE_IDLE ) {
                player.removeListener( playerListener )
                player.release()
            }
        }
    }

    @UnstableApi
    open inner class MusicServiceCallback : MediaLibrarySession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            return MediaSession.ConnectionResult.AcceptedResultBuilder( session )
                .setAvailableSessionCommands( sessionCommands )
                .build()
        }

        @UnstableApi
        override fun onGetLibraryRoot(
            session: MediaLibrarySession, browser: MediaSession.ControllerInfo, params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val isKnownCaller = true
            val rootExtras = Bundle().apply {
                putBoolean( MEDIA_SEARCH_SUPPORTED, true )
                putBoolean( CONTENT_STYLE_SUPPORTED, true )
                putInt( CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_GRID )
                putInt( CONTENT_STYLE_PLAYABLE_HINT, CONTENT_STYLE_LIST )
            }
            val libraryParams = LibraryParams.Builder().setExtras( rootExtras ).build()
            val rootMediaItem = if ( !isKnownCaller ) {
                MediaItem.EMPTY
            }  else {
                catalogueRootMediaItem
            }
            return Futures.immediateFuture( LibraryResult.ofItem( rootMediaItem, libraryParams ) )
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            return Futures.immediateFuture<LibraryResult<ImmutableList<MediaItem>>>(
                LibraryResult.ofItemList(
                    ImmutableList.of(),
                    LibraryParams.Builder().build()
                )
            )
        }

    }

    /** Listen for events from ExoPlayer */
    private inner class PlayerEventListener : Player.Listener {

        override fun onMediaItemTransition( mediaItem: MediaItem?, reason: Int ) {
            super.onMediaItemTransition( mediaItem, reason )
            Timber.tag( TAG ).d( "MEDIA ITEM TRANSITION. ID: ${mediaItem?.mediaId}" )
            mediaItem?.let {
                serviceScope.launch {
                    userPreferencesDataSource.setCurrentlyPlayingSongId( it.mediaId )
                    mostPlayedSongsRepository.addSongId( it.mediaId )
                    playHistoryRepository.upsertSongWithId( it.mediaId, Instant.now() )
                }
            }
        }

        override fun onPlayerError( error: PlaybackException ) {
            var message = R.string.generic_error
            Timber.tag( TAG ).d( "Player error: ${error.errorCodeName} ( ${error.errorCode} )" )
            if ( error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
                || error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ) {
                message = R.string.error_media_not_found
            }
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

/** Content styling constants */
private const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
private const val CONTENT_STYLE_PLAYABLE_HINT = "android.media.browse.CONTENT_STYLE_PLAYABLE_HINT"
private const val CONTENT_STYLE_SUPPORTED = "android.media.browse.CONTENT_STYLE_SUPPORTED"
private const val CONTENT_STYLE_LIST = 1
private const val CONTENT_STYLE_GRID = 2

const val CUSTOM_COMMAND_DELETE_SONG = "com.odesa.musicmatters.delete_song"
private const val TAG = "MUSIC SERVICE TAG"
val EMPTY_MEDIA_ITEM = MediaItem.Builder()
    .setMediaId( UUID.randomUUID().toString() )
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setIsBrowsable( false )
            .setIsPlayable( false )
            .build()
    ).build()

const val MEDIA_STORE_REFRESH_ENDED_INTENT = "MEDIA_STORE_REFRESH_ENDED_INTENT"
const val MEDIA_STORE_REFRESH_STARTED_INTENT = "MEDIA_STORE_REFRESH_STARTED_INTENT"