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
import com.squad.musicmatters.core.designsystem.component.LoopModeButton
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.component.ShuffleButton
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.designsystem.R as designSystemR

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
        ShuffleButton(
            shuffleEnabled = shuffle,
            onToggleShuffleMode = onToggleShuffleMode,
            modifier = Modifier.size(
                MusicMattersIcons.Shuffle.defaultWidth,
                MusicMattersIcons.Shuffle.defaultHeight
            )
        )
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
        LoopModeButton(
            loopMode = loopMode,
            onToggleLoopMode = onToggleLoopMode,
            modifier = Modifier.size(
                MusicMattersIcons.Shuffle.defaultWidth,
                MusicMattersIcons.Shuffle.defaultHeight
            )
        )
    }
}

