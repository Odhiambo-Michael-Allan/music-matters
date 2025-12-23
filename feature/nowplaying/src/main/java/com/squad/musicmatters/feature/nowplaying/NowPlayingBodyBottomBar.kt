package com.squad.musicmatters.feature.nowplaying

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.ScreenOrientation
import com.squad.musicmatters.core.ui.dialog.ScaffoldDialog


@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NowPlayingBodyBottomBar(
    modifier: Modifier = Modifier,
    language: Language,
    currentLoopMode: LoopMode,
    shuffle: Boolean,
    currentSpeed: Float,
    currentPitch: Float,
    onNavigateToQueue: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onSpeedChange: ( Float ) -> Unit,
    onPitchChange: ( Float ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
) {

    var showExtraOptions by remember { mutableStateOf( false ) }
    var showSpeedDialog by remember { mutableStateOf( false ) }
    var showPitchDialog by remember { mutableStateOf( false ) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp,
                end = 4.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { onToggleLoopMode( currentLoopMode ) }
        ) {
            Icon(
                imageVector = when ( currentLoopMode ) {
                    LoopMode.Song -> Icons.Rounded.RepeatOne
                    else -> Icons.Rounded.Repeat
                },
                contentDescription = null,
                tint = when ( currentLoopMode ) {
                    LoopMode.None -> LocalContentColor.current
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
        IconButton(
            onClick = { onToggleShuffleMode( !shuffle ) }
        ) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = null,
                tint = if ( shuffle ) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }
        IconButton(
            onClick = { showExtraOptions = !showExtraOptions }
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = null
            )
        }
    }

    if ( showExtraOptions ) {
        ModalBottomSheet(
            modifier = if ( ScreenOrientation.fromConfiguration(
                    LocalConfiguration.current ).isLandscape )
                Modifier.padding( start = 50.dp ) else Modifier,
            onDismissRequest = {
                showExtraOptions = false
            }
        ) {
            Column {
                Card (
                    onClick = {
                        showExtraOptions = false
                        onCreateEqualizerActivityContract()
                    }
                ) {
                    ListItem(
                        leadingContent = {
                            Icon( imageVector = Icons.Filled.GraphicEq, contentDescription = null )
                        },
                        headlineContent = { Text( text = language.equalizer ) }
                    )
                }
                Card (
                    onClick = {
                        showExtraOptions = false
                        showSpeedDialog = !showSpeedDialog
                    }
                ) {
                    ListItem(
                        leadingContent = {
                            Icon( imageVector = Icons.Outlined.Speed, contentDescription = null )
                        },
                        headlineContent = {
                            Text( text = language.speed )
                        },
                        supportingContent = {
                            Text( text = "x$currentSpeed" )
                        }
                    )
                }
                Card (
                    onClick = {
                        showExtraOptions = false
                        showPitchDialog = !showPitchDialog
                    }
                ) {
                    ListItem(
                        leadingContent = {
                            Icon(imageVector = Icons.Outlined.Speed, contentDescription = null)
                        },
                        headlineContent = {
                            Text(text = language.pitch)
                        },
                        supportingContent = {
                            Text( text = "x$currentPitch" )
                        }
                    )
                }
            }
        }
    }

    if ( showSpeedDialog ) {
        NowPlayingOptionDialog(
            title = language.speed,
            currentValue = currentSpeed,
            onValueChange = onSpeedChange,
            onDismissRequest = { showSpeedDialog = false }
        )
    }

    if ( showPitchDialog ) {
        NowPlayingOptionDialog(
            title = language.pitch,
            currentValue = currentPitch,
            onValueChange = onPitchChange,
            onDismissRequest = { showPitchDialog = false }
        )
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NowPlayingOptionDialog(
    title: String,
    currentValue: Float,
    onValueChange: ( Float ) -> Unit,
    onDismissRequest: () -> Unit
) {
    ScaffoldDialog(
        title = { Text( text = title ) },
        onDismissRequest = onDismissRequest,
        content = {
            Column( modifier = Modifier.padding( 0.dp, 8.dp ) ) {
                setOf( .5f, 1f, 1.5f, 2f ).map {
                    val onClick = {
                        onDismissRequest()
                        onValueChange( it )
                    }
                    Card (
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        shape = MaterialTheme.shapes.small,
                        onClick = onClick
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp, 0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentValue == it,
                                onClick = onClick
                            )
                            Spacer( modifier = Modifier.width( 8.dp ) )
                            Text( text = "x$it" )
                        }
                    }
                }
            }
        }
    )
}

@DevicePreviews
@Composable
fun NowPlayingBodyBottomBarPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        useMaterialYou = true,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME
    ) {
        NowPlayingBodyBottomBar(
            language = English,
            currentLoopMode = LoopMode.Song,
            shuffle = true,
            currentSpeed = 2f,
            currentPitch = 2f,
            onNavigateToQueue = {},
            onToggleLoopMode = {},
            onToggleShuffleMode = {},
            onSpeedChange = {},
            onPitchChange = {}
        ) {
            object : ActivityResultContract<Unit, Unit>() {
                override fun createIntent(
                    context: Context,
                    input: Unit,
                ) = Intent()

                override fun parseResult(resultCode: Int, intent: Intent?) {}
            }
        }
    }
}