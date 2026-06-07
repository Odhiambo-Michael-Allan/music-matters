package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicMatters.core.designsystem.R as designSystemR

@Composable
internal fun NowPlayingPlayerControls(
    isPlaying: Boolean,
    shuffle: Boolean,
    loopMode: LoopMode,
    onPreviousButtonClick: () -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
) {
    Row (
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = shuffle,
            label = "ShuffleAnimation"
        ) { isShuffleEnabled ->
            IconButton(
                onClick = { onToggleShuffleMode( !isShuffleEnabled ) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource( id = designSystemR.drawable.ic_shuffle ),
                        contentDescription = null,
                        tint = if ( isShuffleEnabled ) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            LocalContentColor.current
                        },
                        modifier = Modifier.size(
                            MusicMattersIcons.Shuffle.defaultWidth,
                            MusicMattersIcons.Shuffle.defaultHeight,
                        )
                    )

                    if ( isShuffleEnabled ) {
                        OnIndicator()
                    }
                }
            }
        }
        Spacer( modifier = Modifier.weight( 0.5f ) )
        PlayPreviousSongButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.ExtraLarge
            ),
            onClick = onPreviousButtonClick
        )
        Spacer( modifier = Modifier.width( 8.dp ) )
        PlayPauseButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.ExtraLarge
            ),
            isPlaying = isPlaying,
            onClick = onPausePlayButtonClick
        )
        Spacer( modifier = Modifier.width( 8.dp ) )
        PlayNextButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.ExtraLarge
            ),
            onClick = onNextButtonClick
        )
        Spacer( modifier = Modifier.weight( 0.5f ) )
        AnimatedContent(
            targetState = loopMode,
        ) {
            IconButton(
                onClick = { loopMode.let { onToggleLoopMode( loopMode ) } }
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = when ( it ) {
                                LoopMode.Song -> designSystemR.drawable.ic_repeat_current
                                else -> designSystemR.drawable.ic_repeat
                            }
                        ),
                        contentDescription = null,
                        tint = when ( loopMode ) {
                            LoopMode.None -> LocalContentColor.current
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(
                            MusicMattersIcons.Loop.defaultWidth,
                            MusicMattersIcons.Loop.defaultHeight
                        )
                    )
                    if ( it != LoopMode.None ) {
                        OnIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun OnIndicator() {
    Spacer( modifier = Modifier.height( 1.dp ) )
    Box(
        modifier = Modifier
            .size( 4.dp ) // Exact size of the dot
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )
}