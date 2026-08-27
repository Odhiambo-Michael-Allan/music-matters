package com.squad.musicmatters.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerSettingOnIndicator() {
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