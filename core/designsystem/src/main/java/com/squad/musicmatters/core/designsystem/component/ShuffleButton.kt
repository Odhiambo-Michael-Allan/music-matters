package com.squad.musicmatters.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.squad.musicmatters.core.designsystem.R

@Composable
fun ShuffleButton(
    modifier: Modifier = Modifier,
    shuffleEnabled: Boolean,
    onToggleShuffleMode: ( Boolean ) -> Unit,
) {
    AnimatedContent(
        targetState = shuffleEnabled,
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
                    painter = painterResource( id = R.drawable.ic_shuffle ),
                    contentDescription = null,
                    tint = if ( isShuffleEnabled ) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        LocalContentColor.current
                    },
                    modifier = modifier
                )

                if ( isShuffleEnabled ) {
                    OnIndicator()
                }
            }
        }
    }
}

@Composable
internal fun OnIndicator() {
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