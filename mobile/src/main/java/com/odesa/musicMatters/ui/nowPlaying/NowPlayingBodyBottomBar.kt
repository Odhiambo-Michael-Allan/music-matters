package com.odesa.musicMatters.ui.nowPlaying

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.allowedSpeedPitchValues
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.components.ScaffoldDialog
import com.odesa.musicMatters.ui.settings.components.SettingsTileDefaults
import com.odesa.musicMatters.utils.ScreenOrientation


@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NowPlayingBodyBottomBar(
    language: Language,
    currentSongIndex: Int,
    queueSize: Int,
    currentLoopMode: LoopMode,
    shuffle: Boolean,
    currentSpeed: Float,
    currentPitch: Float,
    onQueueClicked: () -> Unit,
    onToggleLoopMode: () -> Unit,
    onToggleShuffleMode: () -> Unit,
    onSpeedChange: ( Float ) -> Unit,
    onPitchChange: ( Float ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
) {

    var showExtraOptions by remember { mutableStateOf( false ) }
    var showSpeedDialog by remember { mutableStateOf( false ) }
    var showPitchDialog by remember { mutableStateOf( false ) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp,
                end = 4.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = onQueueClicked
        ) {
            Icon(
                imageVector = Icons.Filled.Sort,
                contentDescription = null
            )
            Spacer( modifier = Modifier.width( 4.dp ) )
            Text(
                text = language.playingXofY(
                    ( currentSongIndex + 1 ).toString(),
                    queueSize.toString()
                ),
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer( modifier = Modifier.weight( 1f ) )
        IconButton(
            onClick = onToggleLoopMode
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
            onClick = onToggleShuffleMode
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
            Column ( modifier = Modifier.padding( 0.dp, 8.dp ) ) {
                allowedSpeedPitchValues.map {
                    val onClick = {
                        onDismissRequest()
                        onValueChange( it )
                    }
                    Card (
                        colors = SettingsTileDefaults.cardColors(),
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

@Preview( showSystemUi = true )
@Composable
fun NowPlayingBodyBottomBarPreview() {
    NowPlayingBodyBottomBar(
        language = English,
        currentSongIndex = 3,
        queueSize = 126,
        currentLoopMode = LoopMode.Song,
        shuffle = true,
        currentSpeed = 2f,
        currentPitch = 2f,
        onQueueClicked = {},
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