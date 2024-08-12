package com.odesa.musicMatters

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.odesa.musicMatters.core.common.media.MEDIA_STORE_UPDATED_INTENT
import com.odesa.musicMatters.core.common.media.MediaPermissionsManager
import com.odesa.musicMatters.core.data.utils.VersionUtils
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.di.MobileDiModule
import com.odesa.musicMatters.ui.MusicallyApp
import com.odesa.musicMatters.ui.components.PermissionsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@UnstableApi
class MainActivity : ComponentActivity() {

    private lateinit var mobileDiModule: MobileDiModule

    private var currentSongBeingDeleted: Song? = null // A bit ugly..

    private val deleteResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if ( result.resultCode == RESULT_OK ) deleteCurrentSong()
        currentSongBeingDeleted = null
    }

    private val deleteResultLauncherForApiBelow29 = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if ( isGranted ) deleteCurrentSong()
    }

    private val mediaStoreUpdateBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive( p0: Context?, p1: Intent? ) {
            lifecycleScope.launch {
                Timber.tag( TAG ).d( "RECEIVED MEDIA STORE UPDATE INTENT" )
                mobileDiModule.musicServiceConnection.onMediaStoreChange()
            }
        }

    }

    private fun deleteCurrentSong() {
        lifecycleScope.launch( Dispatchers.IO ) {
            currentSongBeingDeleted?.let {
                mobileDiModule.musicServiceConnection.deleteSong( it )
                contentResolver.delete( it.mediaUri, null, null )
                Toast.makeText(
                    applicationContext,
                    "Song Deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        Timber.plant( Timber.DebugTree() )
        mobileDiModule = ( application as MusicMatters ).diModule

        ContextCompat.registerReceiver( applicationContext, mediaStoreUpdateBroadcastReceiver, IntentFilter( MEDIA_STORE_UPDATED_INTENT ),
            ContextCompat.RECEIVER_NOT_EXPORTED )

        MediaPermissionsManager.checkForPermissions( applicationContext )
        setContent {

            val allRequiredPermissionsHaveBeenGranted by MediaPermissionsManager.hasAllRequiredPermissions.collectAsState()
            val postNotificationsPermissionGranted by MediaPermissionsManager.postNotificationPermissionGranted.collectAsState()
            val readExternalStoragePermissionGranted by MediaPermissionsManager.readExternalStoragePermissionGranted.collectAsState()

            val readMediaAudioPermissionGranted by MediaPermissionsManager.readMediaAudioPermissionGranted.collectAsState()
            var displayPermissionsScreen by remember {
                mutableStateOf( !MediaPermissionsManager.hasAllRequiredPermissions.value )
            }

            val settingsRepository = mobileDiModule.settingsRepository
            val themeMode by settingsRepository.themeMode.collectAsState()
            val primaryColorName by settingsRepository.primaryColorName.collectAsState()

            val font by settingsRepository.font.collectAsState()
            val fontScale by settingsRepository.fontScale.collectAsState()
            val useMaterialYou by settingsRepository.useMaterialYou.collectAsState()

            MusicMattersTheme(
                themeMode = themeMode,
                primaryColorName = primaryColorName,
                fontName = font.name,
                fontScale = fontScale,
                useMaterialYou = useMaterialYou
            ) {

                Surface( color = MaterialTheme.colorScheme.background ) {
                    when {
                        displayPermissionsScreen -> {
                            PermissionsScreen(
                                allRequiredPermissionsHaveBeenGranted = allRequiredPermissionsHaveBeenGranted,
                                postNotificationsPermissionGranted = postNotificationsPermissionGranted,
                                onPostNotificationsPermissionGranted = {
                                    MediaPermissionsManager.postNotificationPermissionGranted(
                                        isGranted = it,
                                        context = applicationContext
                                    )
                                },
                                readExternalStoragePermissionsGranted = readExternalStoragePermissionGranted,
                                onReadExternalStoragePermissionGranted = {
                                    MediaPermissionsManager.readExternalStoragePermissionGranted(
                                        isGranted = it,
                                        context = applicationContext
                                    )
                                },
                                readMediaAudioPermissionGranted = readMediaAudioPermissionGranted,
                                onReadMediaAudioPermissionGranted = {
                                    MediaPermissionsManager.readMediaAudioPermissionGranted(
                                        isGranted = it,
                                        context = applicationContext
                                    )
                                },
                                onLetsGo = {
                                    displayPermissionsScreen = false
                                }
                            )
                        }
                        else -> {
                            MusicallyApp(
                                mainActivity = this,
                                settingsRepository = mobileDiModule.settingsRepository,
                                musicServiceConnection = mobileDiModule.musicServiceConnection,
                                playlistRepository = mobileDiModule.playlistRepository,
                                searchHistoryRepository = mobileDiModule.searchHistoryRepository,
                                songsAdditionalMetadataRepository = mobileDiModule.songsAdditionalMetadataRepository,
                            )
                        }
                    }
                }
            }
        }
    }

    // https://medium.com/@vishrut.goyani9/scoped-storage-in-android-writing-deleting-media-files-ee6235d30117
    fun deleteSong( song: Song ) {
        currentSongBeingDeleted = song
        if ( !VersionUtils.isQandAbove() ) {
            deleteResultLauncherForApiBelow29.launch( Manifest.permission.WRITE_EXTERNAL_STORAGE )
        }
        try {
            Timber.tag( TAG ).d( "SONG URI: ${song.mediaUri}" )
            contentResolver.delete( song.mediaUri, null, null )
            Timber.tag( TAG ).d( "DELETED IN CONTENT RESOLVER" )
        } catch ( securityException: SecurityException ) {
            if ( VersionUtils.isQandAbove() ) {
                val recoverableSecurityException = securityException as RecoverableSecurityException
                val actionIntent = recoverableSecurityException.userAction.actionIntent
                val senderRequest = IntentSenderRequest.Builder(
                    actionIntent.intentSender
                ).build()
                deleteResultLauncher.launch( senderRequest )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver( mediaStoreUpdateBroadcastReceiver )
    }

}

private const val TAG = "--MAIN ACTIVITY TAG--"
