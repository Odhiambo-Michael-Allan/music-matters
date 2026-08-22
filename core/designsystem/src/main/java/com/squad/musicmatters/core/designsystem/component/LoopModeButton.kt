package com.squad.musicmatters.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.squad.musicmatters.core.designsystem.R
import com.squad.musicmatters.core.model.LoopMode

@Composable
fun LoopModeButton(
    modifier: Modifier = Modifier,
    loopMode: LoopMode,
    onToggleLoopMode: ( LoopMode ) -> Unit,
) {
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
                            LoopMode.Song -> R.drawable.ic_repeat_current
                            else -> R.drawable.ic_repeat
                        }
                    ),
                    contentDescription = null,
                    tint = when ( loopMode ) {
                        LoopMode.None -> LocalContentColor.current
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = modifier
                )
                if ( it != LoopMode.None ) {
                    OnIndicator()
                }
            }
        }
    }
}