package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.media.connection.SleepTimer

@Composable
internal fun NowPlayingDefaultControlsLayout(
    isPlaying: Boolean,
    sleepTimer: SleepTimer?,
    onPreviousButtonClick: () -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onShowSleepTimerBottomSheet: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy( 12.dp ),
    ) {
        PlayPauseButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Primary,
                size = NowPlayingControlButtonSize.ExtraLarge
            ),
            isPlaying = isPlaying,
            onClick = onPausePlayButtonClick
        )
        PlayPreviousSongButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.Large
            ),
            onClick = onPreviousButtonClick
        )
        PlayNextButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.Large
            ),
            onClick = onNextButtonClick
        )
        Spacer( modifier = Modifier.weight(0.8f ) )
        SleepTimerButton(
            sleepTimer = sleepTimer,
            onClick = onShowSleepTimerBottomSheet
        )
        IconButton(
            onClick = onNavigateToQueue
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.PlaylistPlay,
                contentDescription = null,
            )
        }
    }
}

@Composable
internal fun NowPlayingTraditionalControlsLayout(
    isPlaying: Boolean,
    sleepTimer: SleepTimer?,
    onPreviousButtonClick: () -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onShowSleepTimerBottomSheet: () -> Unit,
) {
    Row (
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onNavigateToQueue,
        ) {
            Icon(
                imageVector = MusicMattersIcons.Queue,
                contentDescription = null,
            )
        }
        Spacer( modifier = Modifier.weight( 0.5f ) )
        PlayPreviousSongButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.Large
            ),
            onClick = onPreviousButtonClick
        )
        Spacer( modifier = Modifier.width( 8.dp ) )
        PlayPauseButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Primary,
                size = NowPlayingControlButtonSize.ExtraLarge
            ),
            isPlaying = isPlaying,
            onClick = onPausePlayButtonClick
        )
        Spacer( modifier = Modifier.width( 8.dp ) )
        PlayNextButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.Large
            ),
            onClick = onNextButtonClick
        )
        Spacer( modifier = Modifier.weight( 0.5f ) )
        SleepTimerButton(
            sleepTimer = sleepTimer,
            onClick = onShowSleepTimerBottomSheet
        )
    }
}