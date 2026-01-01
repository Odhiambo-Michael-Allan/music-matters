package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.media.connection.SleepTimer

@Composable
internal fun SleepTimerButton(
    sleepTimer: SleepTimer?,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick
    ) {
        AnimatedContent(
            targetState = sleepTimer
        ) { timer ->
            if ( timer != null ) {
                Icon(
                    imageVector = MusicMattersIcons.TimerOn,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
            } else {
                Icon(
                    imageVector = MusicMattersIcons.Timer,
                    contentDescription = null
                )
            }
        }
    }
}







@Composable
internal fun PlayNextButton(
    style: NowPlayingControlButtonStyle,
    onClick: () -> Unit
) {
    NowPlayingControlButton(
        style = style,
        icon = MusicMattersIcons.PlayNext,
        roundedCornerSizeDp = 30.dp
    ) {
        onClick()
    }
}

@Composable
internal fun PlayPreviousSongButton(
    style: NowPlayingControlButtonStyle,
    onClick: () -> Unit
) {
    NowPlayingControlButton(
        style = style,
        icon = MusicMattersIcons.PlayPrevious,
        roundedCornerSizeDp = 30.dp
    ) {
        onClick()
    }
}

@Composable
internal fun PlayPauseButton(
    style: NowPlayingControlButtonStyle,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    AnimatedContent(
        targetState = isPlaying,
        label = "now-playing-play-pause-button"
    ) {
        NowPlayingControlButton(
            modifier = Modifier.size( 60.dp ),
            style = style,
            icon = if ( it ) {
                MusicMattersIcons.Pause
            } else {
                MusicMattersIcons.Play
            },
            roundedCornerSizeDp = 24.dp
        ) {
            onClick()
        }
    }
}

@Composable
private fun NowPlayingControlButton(
    modifier: Modifier = Modifier,
    style: NowPlayingControlButtonStyle,
    icon: ImageVector,
    roundedCornerSizeDp: Dp,
    onClick: () -> Unit
) {
    val backgroundColor = when ( style.color ) {
        NowPlayingControlButtonColors.Primary -> MaterialTheme.colorScheme.primary
        NowPlayingControlButtonColors.Surface -> MaterialTheme.colorScheme.surfaceVariant
        NowPlayingControlButtonColors.Transparent -> Color.Transparent
    }
    val contentColor = when ( style.color ) {
        NowPlayingControlButtonColors.Primary -> MaterialTheme.colorScheme.onPrimary
        else -> LocalContentColor.current
    }
    val iconSize = when ( style.size ) {
        NowPlayingControlButtonSize.Default -> 24.dp
        NowPlayingControlButtonSize.Large -> 32.dp
        NowPlayingControlButtonSize.ExtraLarge -> 36.dp
    }
    IconButton(
        modifier = modifier
            .background(
                backgroundColor,
                RoundedCornerShape( roundedCornerSizeDp )
            ),
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size( iconSize )
        )
    }
}

internal data class NowPlayingControlButtonStyle(
    val color: NowPlayingControlButtonColors,
    val size: NowPlayingControlButtonSize = NowPlayingControlButtonSize.Default
)

internal enum class NowPlayingControlButtonColors {
    Primary,
    Surface,
    Transparent
}

internal enum class NowPlayingControlButtonSize {
    Default,
    Large,
    ExtraLarge,
}