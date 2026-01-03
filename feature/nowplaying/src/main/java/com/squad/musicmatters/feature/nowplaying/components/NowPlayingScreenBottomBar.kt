package com.squad.musicmatters.feature.nowplaying.components

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squad.musicMatters.core.designsystem.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.ScreenOrientation
import com.squad.musicmatters.core.ui.dialog.ScaffoldDialog


@OptIn( ExperimentalMaterial3Api::class )
@Composable
internal fun NowPlayingScreenBottomBar(
    modifier: Modifier = Modifier,
    language: Language,
    currentLoopMode: LoopMode,
    shuffle: Boolean,
    currentSpeed: Float,
    currentPitch: Float,
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
                painter = painterResource(
                    id = when ( currentLoopMode ) {
                        LoopMode.Song -> R.drawable.repeat_current
                        else -> R.drawable.repeat
                    }
                ),
                contentDescription = null,
                tint = when ( currentLoopMode ) {
                    LoopMode.None -> LocalContentColor.current
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(
                    MusicMattersIcons.Loop.defaultWidth,
                    MusicMattersIcons.Loop.defaultHeight
                )
            )
        }
        IconButton(
            onClick = {}
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_lyrics_outline,
                ),
                contentDescription = null,
            )
        }
        AnimatedContent(
            targetState = shuffle
        ) {
            IconButton(
                onClick = { onToggleShuffleMode( !shuffle ) }
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource( id = R.drawable.shuffle ),
                        contentDescription = null,
                        tint = if ( it ) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            LocalContentColor.current
                        },
                        modifier = Modifier.size(
                            MusicMattersIcons.Shuffle.defaultWidth,
                            MusicMattersIcons.Shuffle.defaultHeight,
                        )
                    )
                    if ( it ) {
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 0.1.sp
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = { showExtraOptions = !showExtraOptions }
        ) {
            Icon(
                imageVector = MusicMattersIcons.MoreHorizontal,
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
                            Icon( imageVector = MusicMattersIcons.Equalizer, contentDescription = null )
                        },
                        headlineContent = {
                            Text(
                                text = language.equalizer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
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
                            Icon( imageVector = MusicMattersIcons.Speed, contentDescription = null )
                        },
                        headlineContent = {
                            Text(
                                text = language.speed,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "x$currentSpeed",
                                fontWeight = FontWeight.SemiBold
                            )
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
                            Icon( imageVector = MusicMattersIcons.Speed, contentDescription = null)
                        },
                        headlineContent = {
                            Text(
                                text = language.pitch,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "x$currentPitch",
                                fontWeight = FontWeight.SemiBold
                            )
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
private fun NowPlayingOptionDialog(
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
private fun NowPlayingScreenBottomBarPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        useMaterialYou = true,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME
    ) {
        NowPlayingScreenBottomBar(
            language = English,
            currentLoopMode = LoopMode.Song,
            shuffle = true,
            currentSpeed = 2f,
            currentPitch = 2f,
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