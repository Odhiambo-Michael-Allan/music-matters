package com.odesa.musicMatters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.odesa.musicMatters.core.common.media.MediaPermissionsManager
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.di.MobileDiModule
import com.odesa.musicMatters.ui.MusicallyApp
import com.odesa.musicMatters.ui.components.PermissionsScreen
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private lateinit var mobileDiModule: MobileDiModule

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
        // Allow app to draw behind system bar decorations (e.g.: navbar)
//        WindowCompat.setDecorFitsSystemWindows( window, false )
        Timber.plant( Timber.DebugTree() )
        mobileDiModule = ( application as MusicMatters ).diModule

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
}
