package com.squad.musicmatters.ui.components

import android.Manifest
import android.content.Context
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.i8n.R
import com.squad.musicmatters.core.data.songs.MediaPermissionsManager
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import kotlinx.coroutines.CoroutineScope
import com.squad.musicmatters.core.i8n.R as i8nR
import kotlinx.coroutines.launch


@OptIn( ExperimentalLayoutApi::class )
@Composable
fun PermissionsScreen(
    onNavigateToApplicationDetailsSettings: () -> Unit,
    onLetsGo: () -> Unit,
) {

    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val allRequiredPermissionsHaveBeenGranted by MediaPermissionsManager
        .hasAllRequiredPermissions
        .collectAsState()
    val postNotificationsPermissionGranted by MediaPermissionsManager
        .postNotificationPermissionGranted
        .collectAsState()
    val readExternalStoragePermissionGranted by MediaPermissionsManager
        .readExternalStoragePermissionGranted
        .collectAsState()

    val readMediaAudioPermissionGranted by MediaPermissionsManager
        .readMediaAudioPermissionGranted
        .collectAsState()

    val readExternalStoragePermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        MediaPermissionsManager.readExternalStoragePermissionGranted(
            isGranted = granted,
            context = context,
        )
        coroutineScope.showSnackBar(
            show = !granted,
            context = context,
            snackBarHostState = snackBarHostState,
            message = context
                .getString( i8nR.string.core_i8n_storage_access_permission_denied ),
            onNavigateToApplicationDetailsSettings = onNavigateToApplicationDetailsSettings,
        )
    }

    val readMediaAudioPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        MediaPermissionsManager.readMediaAudioPermissionGranted(
            isGranted = granted,
            context = context,
        )
        coroutineScope.showSnackBar(
            show = granted.not(),
            context = context,
            snackBarHostState = snackBarHostState,
            message = context
                .getString( i8nR.string.core_i8n_read_media_audio_permission_denied ),
            onNavigateToApplicationDetailsSettings = onNavigateToApplicationDetailsSettings,
        )
    }

    val postNotificationsPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        MediaPermissionsManager.postNotificationPermissionGranted(
            isGranted = granted,
            context = context,
        )
        coroutineScope.showSnackBar(
            show = granted.not(),
            context = context,
            snackBarHostState = snackBarHostState,
            message = context
                .getString( i8nR.string.core_i8n_post_notifications_permission_denied ),
            onNavigateToApplicationDetailsSettings = onNavigateToApplicationDetailsSettings,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost( hostState = snackBarHostState ) }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight( 1f )
                    .padding( 16.dp ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer( modifier = Modifier.height( 48.dp ) )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource( id = R.string.core_i8n_welcome_message ),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding( end = 8.dp ),
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
                        title = stringResource( id = R.string.core_i8n_read_media_audio ),
                        permissionGranted = readMediaAudioPermissionGranted,
                        description = stringResource(
                            id = R.string.core_i8n_read_media_audio_permission_prompt
                        )
                    ) {
                        readMediaAudioPermissionRequestLauncher
                            .launch( Manifest.permission.READ_MEDIA_AUDIO )
                    }
                    PermissionCard(
                        title = stringResource(
                            id = R.string.core_i8n_post_notifications_permission
                        ),
                        permissionGranted = postNotificationsPermissionGranted,
                        description = stringResource(
                            id = R.string.core_i8n_post_notifications_permission_prompt
                        )
                    ) {
                        postNotificationsPermissionRequestLauncher
                            .launch( Manifest.permission.POST_NOTIFICATIONS )
                    }
                }
                else {
                    PermissionCard(
                        title = stringResource( id = R.string.core_i8n_storage_access ),
                        description = stringResource( id = R.string.core_i8n_storage_access_permission_prompt ),
                        permissionGranted = readExternalStoragePermissionGranted,
                        onClick = {
                            readExternalStoragePermissionRequestLauncher
                                .launch( Manifest.permission.READ_EXTERNAL_STORAGE )
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
                        text = stringResource( id = R.string.core_i8n_lets_go ),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding( 8.dp ),
                    )
                }
            }
            Spacer( modifier = Modifier.height( 50.dp ) )
        }
    }
}

private fun CoroutineScope.showSnackBar(
    show: Boolean,
    context: Context,
    snackBarHostState: SnackbarHostState,
    message: String,
    onNavigateToApplicationDetailsSettings: () -> Unit,
) {
    if ( show ) {
        snackBarHostState.currentSnackbarData?.dismiss()
        launch {
            val result = snackBarHostState.showSnackbar(
                message = message,
                actionLabel = context.getString( i8nR.string.core_i8n_settings ),
            )
            if ( result == SnackbarResult.ActionPerformed ) {
                onNavigateToApplicationDetailsSettings()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun PermissionsScreenPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        PermissionsScreen(
            onNavigateToApplicationDetailsSettings = {},
            onLetsGo = {}
        )
    }
}