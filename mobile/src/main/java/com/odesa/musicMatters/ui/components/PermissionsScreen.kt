package com.odesa.musicMatters.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme

@OptIn( ExperimentalLayoutApi::class )
@Composable
fun PermissionsScreen(
    allRequiredPermissionsHaveBeenGranted: Boolean,
    readExternalStoragePermissionsGranted: Boolean,
    onReadExternalStoragePermissionGranted: ( Boolean ) -> Unit,
    readMediaAudioPermissionGranted: Boolean,
    onReadMediaAudioPermissionGranted: ( Boolean ) -> Unit,
    postNotificationsPermissionGranted: Boolean,
    onPostNotificationsPermissionGranted: ( Boolean ) -> Unit,
    onLetsGo: () -> Unit,
) {

    val readExternalStoragePermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onReadExternalStoragePermissionGranted( isGranted )
    }

    val readMediaAudioPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onReadMediaAudioPermissionGranted( isGranted )
    }

    val postNotificationsPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPostNotificationsPermissionGranted( isGranted )
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier
                .verticalScroll( rememberScrollState() )
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer( modifier = Modifier.height( 48.dp ) )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.padding( end = 8.dp ),
                    text = stringResource( id = R.string.welcome_message ),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Music",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Matters",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            HorizontalDivider()

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
                PermissionCard(
                    title = stringResource( id = R.string.read_media_audio ),
                    permissionGranted = readMediaAudioPermissionGranted,
                    description = stringResource( id = R.string.read_media_audio_prompt )
                ) {
                    readMediaAudioPermissionRequestLauncher.launch( Manifest.permission.READ_MEDIA_AUDIO )
                }
                PermissionCard(
                    title = stringResource( id = R.string.post_notifications_permission ),
                    permissionGranted = postNotificationsPermissionGranted,
                    description = stringResource( id = R.string.post_notifications_prompt )
                ) {
                    postNotificationsPermissionRequestLauncher.launch( Manifest.permission.POST_NOTIFICATIONS )
                }
            }
            else {
                PermissionCard(
                    title = stringResource( id = R.string.storage_access ),
                    description = stringResource( id = R.string.storage_access_prompt ),
                    permissionGranted = readExternalStoragePermissionsGranted,
                    onClick = {
                        readExternalStoragePermissionRequestLauncher.launch( Manifest.permission.READ_EXTERNAL_STORAGE )
                    }
                )
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                enabled = allRequiredPermissionsHaveBeenGranted,
                onClick = onLetsGo
            ) {
                Text(
                    modifier = Modifier.padding( 8.dp ),
                    text = stringResource( id = R.string.lets_go )
                )
            }
        }
        Spacer( modifier = Modifier.height( 50.dp ) )
    }
}

@PreviewScreenSizes
@Composable
fun PermissionsScreenPreview() {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = SettingsDefaults.USE_MATERIAL_YOU
    ) {
        PermissionsScreen(
            allRequiredPermissionsHaveBeenGranted = true,
            readMediaAudioPermissionGranted = true,
            onReadExternalStoragePermissionGranted = {},
            postNotificationsPermissionGranted = true,
            onPostNotificationsPermissionGranted = {},
            readExternalStoragePermissionsGranted = true,
            onReadMediaAudioPermissionGranted = {},
            onLetsGo = {}
        )
    }
}