package com.squad.musicmatters

import android.Manifest
import android.app.RecoverableSecurityException
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import com.squad.musicmatters.core.data.songs.MediaPermissionsManager
import com.squad.musicmatters.core.data.utils.VersionUtils
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.ui.MusicMattersApp
import com.squad.musicmatters.ui.components.PermissionsScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * For now, extend from AppCompatActivity. Otherwise, setApplicationLocales will do nothing.
 * Extending from AppCompatActivity requires to use an AppCompat theme for the Activity. In
 * the manifest, for the activity, use android:theme="@style/Theme.AppCompat". Otherwise,
 * the application will crash.
 *
 * The alternative is to replace AppCompatDelegate with the Framework APIs. The Framework
 * APIs are not backwards compatible, like AppCompatDelegate, and so work for T+.
 * However, with the Framework APIs, you can use Compose themes and extend from
 * ComponentActivity.
 * Framework APIs: https://developer.android.com/about/versions/13/features/app-languages#framework-impl
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
//    private lateinit var nowPlayingViewModel: NowPlayingViewModel

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

//    private val mediaStoreRefreshStartedBroadcastReceiver = MediaStoreUpdateBroadcastReceiver {
//        Timber.tag( TAG ).d( "RECEIVED MEDIA STORE REFRESH STARTED BROADCAST" )
//        mainActivityViewModel.onMediaStoreRefreshStarted()
//    }

//    private val mediaStoreRefreshEndedBroadcastReceiver = MediaStoreUpdateBroadcastReceiver {
//        Timber.tag( TAG ).d( "RECEIVED MEDIA STORE REFRESH ENDED BROADCAST" )
//        mainActivityViewModel.onMediaStoreRefreshEnded()
//    }

    private fun deleteCurrentSong() {
        lifecycleScope.launch {
            currentSongBeingDeleted?.let {
//                mobileDiModule.musicServiceConnection.deleteSong( it )
                contentResolver.delete( it.mediaUri.toUri(), null, null )
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

        var uiState: MainActivityUiState by mutableStateOf( MainActivityUiState.Loading )

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle( Lifecycle.State.STARTED ) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

//        mobileDiModule = ( application as MusicMatters ).diModule

//        val mainActivityViewModelFactory = MainActivityViewModelFactory(
//            mobileDiModule.musicServiceConnection
//        )

//        mainActivityViewModel = ViewModelProvider(
//            this,
//            factory = mainActivityViewModelFactory
//        ).get( MainActivityViewModel::class.java )

//        val nowPlayingViewModelFactory = NowPlayingViewModelFactory(
//            settingsRepository = mobileDiModule.settingsRepository,
//            playlistRepository = mobileDiModule.playlistRepository,
//            songsAdditionalMetadataRepository = mobileDiModule.songsAdditionalMetadataRepository,
//            musicServiceConnection = mobileDiModule.musicServiceConnection
//        )

//        nowPlayingViewModel  = ViewModelProvider(
//            this,
//            factory = nowPlayingViewModelFactory
//        ).get( NowPlayingViewModel::class.java )

//        ContextCompat.registerReceiver(
//            this,
//            mediaStoreRefreshStartedBroadcastReceiver,
//            IntentFilter( MEDIA_STORE_REFRESH_STARTED_INTENT ),
//            ContextCompat.RECEIVER_NOT_EXPORTED
//        )

//        ContextCompat.registerReceiver(
//            this,
//            mediaStoreRefreshEndedBroadcastReceiver,
//            IntentFilter( MEDIA_STORE_REFRESH_ENDED_INTENT ),
//            ContextCompat.RECEIVER_NOT_EXPORTED
//        )

        MediaPermissionsManager.checkForPermissions( applicationContext )

        /**
         * Turn off the decor fitting system windows, which allows us to handle insets, including
         * IME animations, and go edge-to-edge. This also sets up the initial system bar style
         * based on the platform theme.
         */
        enableEdgeToEdge()

        setContent {
            val isDarkTheme = shouldUseDarkTheme( uiState )
            /**
             * Update the edge to edge configuration to match the theme. This is the same parameters
             * as the default enableEdgeToEdge call, but we manually resolve whether or not to show
             * dark theme using uiState, since it can be different that the configuration's dark
             * theme value based on the user preference.
             */
            DisposableEffect(
                key1 = isDarkTheme
            ) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = lightScrim,
                        darkScrim = darkScrim
                    ) { isDarkTheme }
                )
                onDispose {}
            }

            val allRequiredPermissionsHaveBeenGranted by MediaPermissionsManager.hasAllRequiredPermissions.collectAsState()
            val postNotificationsPermissionGranted by MediaPermissionsManager.postNotificationPermissionGranted.collectAsState()
            val readExternalStoragePermissionGranted by MediaPermissionsManager.readExternalStoragePermissionGranted.collectAsState()

            val readMediaAudioPermissionGranted by MediaPermissionsManager.readMediaAudioPermissionGranted.collectAsState()
            var displayPermissionsScreen by remember {
                mutableStateOf( !MediaPermissionsManager.hasAllRequiredPermissions.value )
            }

//            val settingsRepository = mobileDiModule.settingsRepository
//            val themeMode by settingsRepository.themeMode.collectAsState()
//            val primaryColorName by settingsRepository.primaryColorName.collectAsState()
//
//            val font by settingsRepository.font.collectAsState()
//            val fontScale by settingsRepository.fontScale.collectAsState()
//            val useMaterialYou by settingsRepository.useMaterialYou.collectAsState()

            MusicMattersTheme(
                themeMode = getThemeMode( uiState ),
                primaryColorName = getPrimaryColorName( uiState ),
                fontName = getFontName( uiState ),
                fontScale = getFontScale( uiState ),
                useMaterialYou = useMaterialYou( uiState )
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
                            MusicMattersApp(
                                uiState = uiState,
                                onDeleteSong = { deleteSong( it ) }
//                                nowPlayingViewModel = nowPlayingViewModel,
//                                settingsRepository = mobileDiModule.settingsRepository,
//                                musicServiceConnection = musicServiceConnection,
//                                playlistRepository = mobileDiModule.playlistRepository,
//                                searchHistoryRepository = mobileDiModule.searchHistoryRepository,
//                                songsAdditionalMetadataRepository = mobileDiModule.songsAdditionalMetadataRepository,
                            )
                        }
                    }
                }
            }
        }
    }

    // https://medium.com/@vishrut.goyani9/scoped-storage-in-android-writing-deleting-media-files-ee6235d30117
    private fun deleteSong( song: Song ) {
        currentSongBeingDeleted = song
        if ( !VersionUtils.isQandAbove() ) {
            deleteResultLauncherForApiBelow29.launch( Manifest.permission.WRITE_EXTERNAL_STORAGE )
        }
        try {
            Timber.tag( TAG ).d( "SONG URI: ${song.mediaUri}" )
            contentResolver.delete( song.mediaUri.toUri(), null, null )
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
//        unregisterReceiver( mediaStoreRefreshStartedBroadcastReceiver )
//        unregisterReceiver( mediaStoreRefreshEndedBroadcastReceiver )
    }

}

//private class MainActivityViewModel(
//    private val musicServiceConnection: MusicServiceConnection
//) : ViewModel() {
//    fun onMediaStoreRefreshStarted() {
//        viewModelScope.launch {
//            musicServiceConnection.onMediaStoreRefreshStarted()
//        }
//    }
//
//    fun onMediaStoreRefreshEnded() {
//        viewModelScope.launch {
//            musicServiceConnection.onMediaStoreChange()
//        }
//    }
//}

//@Suppress( "UNCHECKED_CAST" )
//private class MainActivityViewModelFactory(
//    private val musicServiceConnection: MusicServiceConnection
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create( modelClass: Class<T> ): T {
//        return MainActivityViewModel( musicServiceConnection = musicServiceConnection ) as T
//    }
//}

/**
 * Returns 'true' if dark theme should be used, as a function of the [uiState] and the current
 * system context.
 */
@Composable
private fun shouldUseDarkTheme(
    uiState: MainActivityUiState
): Boolean = when ( uiState ) {
    MainActivityUiState.Loading -> isSystemInDarkTheme()
    is MainActivityUiState.Success -> when ( uiState.userData.themeMode ) {
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.BLACK -> true
    }
}
@Composable
private fun getThemeMode(
    uiState: MainActivityUiState
): ThemeMode = when ( uiState ) {
    MainActivityUiState.Loading -> DefaultPreferences.THEME_MODE
    is MainActivityUiState.Success -> uiState.userData.themeMode
}

@Composable
private fun getPrimaryColorName(
    uiState: MainActivityUiState
): String = when ( uiState ) {
    MainActivityUiState.Loading -> DefaultPreferences.PRIMARY_COLOR_NAME
    is MainActivityUiState.Success -> uiState.userData.primaryColorName
}

@Composable
private fun getFontName(
    uiState: MainActivityUiState
): String = when ( uiState ) {
    MainActivityUiState.Loading -> DefaultPreferences.FONT_NAME
    is MainActivityUiState.Success -> uiState.userData.fontName
}

@Composable
private fun getFontScale(
    uiState: MainActivityUiState
): Float = when ( uiState ) {
    MainActivityUiState.Loading -> DefaultPreferences.FONT_SCALE
    is MainActivityUiState.Success -> uiState.userData.fontScale
}

@Composable
private fun useMaterialYou(
    uiState: MainActivityUiState
): Boolean = when ( uiState ) {
    MainActivityUiState.Loading -> false
    is MainActivityUiState.Success -> uiState.userData.useMaterialYou
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb( 0xe6, 0xFF, 0xFF, 0xFF )

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb( 0x80, 0x1b, 0x1b, 0x1b )

private const val TAG = "--MAIN ACTIVITY TAG--"
